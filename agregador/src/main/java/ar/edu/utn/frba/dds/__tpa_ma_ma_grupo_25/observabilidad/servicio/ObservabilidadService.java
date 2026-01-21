package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.servicio;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servicio centralizado de observabilidad que recolecta métricas de todos los microservicios
 */
@Service
public class ObservabilidadService {

    private static final Logger logger = LoggerFactory.getLogger(ObservabilidadService.class);
    private final RestTemplate restTemplate;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;

    // Mapa para cachear métricas en memoria
    private final Map<String, ServiceMetricsDTO> metricsCache = new ConcurrentHashMap<>();

    @Value("${fuente.estatica.url:http://localhost:8080}")
    private String fuenteEstaticaUrl;

    @Value("${fuente.dinamica.url:http://localhost:8081}")
    private String fuenteDinamicaUrl;

    @Value("${fuente.proxy.url:http://localhost:8080}")
    private String fuenteProxyUrl;

    @Value("${estadisticas.url:http://localhost:8081}")
    private String estadisticasUrl;

    @Value("${agregador.url:http://localhost:8081}")
    private String agregadorUrl;

    public ObservabilidadService(MeterRegistry meterRegistry) {
        this.restTemplate = new RestTemplate();
        this.meterRegistry = meterRegistry;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Verifica el estado de salud de todos los microservicios
     */
    public List<ServiceHealthDTO> checkServicesHealth() {
        List<ServiceHealthDTO> healthList = new ArrayList<>();

        healthList.add(checkServiceHealth("Fuente Estática", fuenteEstaticaUrl + "/actuator/health"));
        healthList.add(checkServiceHealth("Fuente Dinámica", fuenteDinamicaUrl + "/actuator/health"));
        healthList.add(checkServiceHealth("Fuente Proxy", fuenteProxyUrl + "/actuator/health"));
        healthList.add(checkServiceHealth("Estadísticas", estadisticasUrl + "/actuator/health"));
        healthList.add(checkServiceHealth("Agregador", agregadorUrl + "/actuator/health"));

        return healthList;
    }

    /**
     * Verifica el estado de salud de un microservicio específico
     */
    private ServiceHealthDTO checkServiceHealth(String serviceName, String healthUrl) {
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> response = restTemplate.getForObject(healthUrl, Map.class);
            long responseTime = System.currentTimeMillis() - startTime;

            String status = response != null && "UP".equals(response.get("status")) ? "UP" : "DOWN";

            ServiceHealthDTO health = new ServiceHealthDTO(serviceName, status, responseTime, healthUrl);

            logger.debug("Health check for {} - Status: {}, Response time: {}ms", serviceName, status, responseTime);

            return health;

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            ServiceHealthDTO health = new ServiceHealthDTO(serviceName, "DOWN", responseTime, healthUrl);
            health.setErrorMessage(e.getMessage());

            logger.warn("Health check failed for {}: {}", serviceName, e.getMessage());

            return health;
        }
    }

    /**
     * Obtiene métricas de todos los microservicios
     */
    public List<ServiceMetricsDTO> getServicesMetrics() {
        List<ServiceMetricsDTO> metricsList = new ArrayList<>();

        metricsList.add(getServiceMetrics("Fuente Estática", fuenteEstaticaUrl + "/actuator/metrics"));
        metricsList.add(getServiceMetrics("Fuente Dinámica", fuenteDinamicaUrl + "/actuator/metrics"));
        metricsList.add(getServiceMetrics("Fuente Proxy", fuenteDinamicaUrl + "/actuator/metrics"));
        metricsList.add(getServiceMetrics("Estadísticas", estadisticasUrl + "/actuator/metrics"));
        metricsList.add(getServiceMetrics("Agregador", agregadorUrl + "/actuator/metrics"));

        return metricsList;
    }

    /**
     * Obtiene métricas de un microservicio específico
     */
    private ServiceMetricsDTO getServiceMetrics(String serviceName, String metricsUrl) {
        ServiceMetricsDTO metrics = new ServiceMetricsDTO(serviceName);

        try {
            // Obtener métricas HTTP
            String httpRequestsUrl = metricsUrl + "/http.server.requests";
            Map<String, Object> httpMetrics = restTemplate.getForObject(httpRequestsUrl, Map.class);

            if (httpMetrics != null && httpMetrics.containsKey("measurements")) {
                List<Map<String, Object>> measurements = (List<Map<String, Object>>) httpMetrics.get("measurements");
                for (Map<String, Object> measurement : measurements) {
                    String statistic = (String) measurement.get("statistic");
                    Number value = (Number) measurement.get("value");

                    if ("COUNT".equals(statistic)) {
                        metrics.setTotalRequests(value.longValue());
                    } else if ("TOTAL_TIME".equals(statistic) && metrics.getTotalRequests() != null && metrics.getTotalRequests() > 0) {
                        metrics.setAvgResponseTimeMs(value.doubleValue() / metrics.getTotalRequests() * 1000);
                    }
                }
            }

            // Obtener uso de memoria
            String memoryUrl = metricsUrl + "/jvm.memory.used";
            Map<String, Object> memoryMetrics = restTemplate.getForObject(memoryUrl, Map.class);

            if (memoryMetrics != null && memoryMetrics.containsKey("measurements")) {
                List<Map<String, Object>> measurements = (List<Map<String, Object>>) memoryMetrics.get("measurements");
                if (!measurements.isEmpty()) {
                    Number value = (Number) measurements.get(0).get("value");
                    metrics.setMemoryUsedMb(value.doubleValue() / (1024 * 1024));
                }
            }

            // Calcular tasa de errores (simplificado)
            if (metrics.getTotalRequests() != null && metrics.getTotalRequests() > 0) {
                // Aquí podrías obtener métricas de errores reales
                metrics.setErrorRate(0.0); // Placeholder
            }

            // Cachear métricas
            metricsCache.put(serviceName, metrics);

        } catch (Exception e) {
            logger.error("Error getting metrics for {}: {}", serviceName, e.getMessage());

            // Retornar desde caché si está disponible
            if (metricsCache.containsKey(serviceName)) {
                return metricsCache.get(serviceName);
            }
        }

        return metrics;
    }

    /**
     * Lee los logs recientes de todos los microservicios
     */
    public List<LogEntryDTO> getRecentLogs(int limit) {
        List<LogEntryDTO> allLogs = new ArrayList<>();

        // Leer logs de cada servicio
        allLogs.addAll(readLogsFromFile("logs/agregador.json", "agregador", limit));
        allLogs.addAll(readLogsFromFile("logs/fuente-estatica.json", "fuente-estatica", limit));
        allLogs.addAll(readLogsFromFile("logs/fuente-dinamica.json", "fuente-dinamica", limit));
        allLogs.addAll(readLogsFromFile("logs/fuente-proxy.json", "fuente-proxy", limit));
        allLogs.addAll(readLogsFromFile("logs/estadisticas.json", "estadisticas", limit));

        // Ordenar por timestamp descendente y limitar
        return allLogs.stream()
                .sorted(Comparator.comparing(LogEntryDTO::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Lee logs desde un archivo JSON
     */
    private List<LogEntryDTO> readLogsFromFile(String filePath, String serviceName, int limit) {
        List<LogEntryDTO> logs = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return logs;
        }

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            logs = lines
                    .limit(limit)
                    .map(line -> parseLogLine(line, serviceName))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error reading logs from {}: {}", filePath, e.getMessage());
        }

        return logs;
    }

    /**
     * Parsea una línea de log JSON a LogEntryDTO
     */
    private LogEntryDTO parseLogLine(String line, String serviceName) {
        try {
            JsonNode json = objectMapper.readTree(line);

            LogEntryDTO log = new LogEntryDTO();
            log.setService(serviceName);
            log.setLevel(json.has("level") ? json.get("level").asText() : "INFO");
            log.setMessage(json.has("message") ? json.get("message").asText() : "");

            if (json.has("@timestamp")) {
                String timestamp = json.get("@timestamp").asText();
                log.setTimestamp(LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME));
            }

            if (json.has("correlationId")) {
                log.setCorrelationId(json.get("correlationId").asText());
            }

            if (json.has("endpoint")) {
                log.setEndpoint(json.get("endpoint").asText());
            }

            if (json.has("httpMethod")) {
                log.setHttpMethod(json.get("httpMethod").asText());
            }

            if (json.has("httpStatus")) {
                log.setHttpStatus(json.get("httpStatus").asInt());
            }

            if (json.has("duration")) {
                log.setDurationMs(json.get("duration").asLong());
            }

            return log;

        } catch (Exception e) {
            logger.debug("Error parsing log line: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Genera un resumen del sistema completo
     */
    public SystemSummaryDTO getSystemSummary(List<ServiceHealthDTO> healthList, List<ServiceMetricsDTO> metricsList) {
        SystemSummaryDTO summary = new SystemSummaryDTO();

        summary.setTotalServices(healthList.size());
        summary.setServicesUp((int) healthList.stream().filter(h -> "UP".equals(h.getStatus())).count());
        summary.setServicesDown((int) healthList.stream().filter(h -> "DOWN".equals(h.getStatus())).count());

        long totalRequests = metricsList.stream()
                .map(ServiceMetricsDTO::getTotalRequests)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        summary.setTotalRequests(totalRequests);

        double avgResponseTime = metricsList.stream()
                .map(ServiceMetricsDTO::getAvgResponseTimeMs)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        summary.setAvgResponseTimeMs(avgResponseTime);

        // Calcular errores (simplificado)
        summary.setTotalErrors(0L);
        summary.setErrorRatePercent(0.0);

        return summary;
    }

    /**
     * Obtiene el dashboard completo de observabilidad
     */
    public DashboardDTO getDashboard() {
        List<ServiceHealthDTO> health = checkServicesHealth();
        List<ServiceMetricsDTO> metrics = getServicesMetrics();
        List<LogEntryDTO> logs = getRecentLogs(50);
        SystemSummaryDTO summary = getSystemSummary(health, metrics);

        return new DashboardDTO(health, metrics, logs, summary);
    }
}

