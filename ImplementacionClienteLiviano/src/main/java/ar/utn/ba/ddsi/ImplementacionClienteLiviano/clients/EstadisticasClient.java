package ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.EstadisticasDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.internal.WebApiCallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class EstadisticasClient {

    private final RestTemplate restTemplate;
    private final AgregadorClient agregadorClient;
    private final FuenteProxyClient fuenteProxyClient;
    private final FuenteEstaticaClient fuenteEstaticaClient;
    private final FuenteDinamicaClient fuenteDinamicaClient;
    private final WebApiCallerService webApiCallerService;
    private final WebClient webClient;
    private final String estadisticasUrl;

    public EstadisticasClient(RestTemplate restTemplate,
                              AgregadorClient agregadorClient,
                              FuenteProxyClient fuenteProxyClient,
                              FuenteEstaticaClient fuenteEstaticaClient,
                              FuenteDinamicaClient fuenteDinamicaClient,
                              WebApiCallerService webApiCallerService,
                              @Value("${estadisticas.url}") String estadisticasUrl) {
        this.restTemplate = restTemplate;
        this.agregadorClient = agregadorClient;
        this.fuenteProxyClient = fuenteProxyClient;
        this.fuenteEstaticaClient = fuenteEstaticaClient;
        this.fuenteDinamicaClient = fuenteDinamicaClient;
        this.webApiCallerService = webApiCallerService;

        String urlFinal = estadisticasUrl;
        if (!estadisticasUrl.startsWith("http://") && !estadisticasUrl.startsWith("https://")) {
            urlFinal = "https://" + estadisticasUrl;
            log.info("⚙️ Agregando protocolo HTTPS a la URL del estadisticas");
        }

        log.info("🔗 Configurando EstadisticasClient con URL: {}", urlFinal);

        // Extraer host y puerto de la URL
        try {
            String[] urlParts = urlFinal.replace("https://", "").replace("http://", "").split("/")[0].split(":");
            String host = urlParts[0];
            String port = urlParts.length > 1 ? urlParts[1] : (urlFinal.startsWith("https://") ? "443" : "80");
            log.info("📍 Host: {}, Puerto: {}", host, port);
        } catch (Exception e) {
            log.warn("⚠️ No se pudo parsear la URL para mostrar host/puerto: {}", e.getMessage());
        }

        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024)) // 32 MB
            .build();
        this.webClient = WebClient.builder()
            .baseUrl(urlFinal)
            .exchangeStrategies(strategies)
            .build();
        this.estadisticasUrl = urlFinal;
    }

    /**
     * Obtiene estadísticas consolidadas de todos los microservicios para el panel de control
     * NOTA: Este método obtiene las estadísticas del PANEL DE CONTROL (resúmenes).
     * NO confundir con el módulo de Estadísticas (puerto 8085) que es diferente.
     */
    public EstadisticasDTO obtenerEstadisticasPanelControl() {
        log.info("=== Obteniendo estadísticas para PANEL DE CONTROL ===");

        EstadisticasDTO estadisticas = new EstadisticasDTO();

        // Variables para acumular solicitudes
        Long solCreacionAceptadas = 0L;
        Long solCreacionRechazadas = 0L;
        Long solCreacionPendientes = 0L;
        Long solCreacionPendientesMod = 0L;
        Long solModificacionAceptadas = 0L;
        Long solModificacionRechazadas = 0L;
        Long solModificacionPendientes = 0L;
        Long solModificacionPendientesMod = 0L;

        // 1. FuenteProxy (hechos cargados desde fuentes externas)
        try {
            Map<String, Object> stats = fuenteProxyClient.obtenerEstadisticas();
            if (stats != null && !stats.isEmpty()) {
                Long hechos = getLongValue(stats, "hechosImportados");
                estadisticas.setHechosCargados(hechos);
                log.info("✓ FuenteProxy: hechosCargados={}", hechos);
            } else {
                log.warn("⚠ FuenteProxy: respuesta vacía");
                estadisticas.setHechosCargados(0L);
            }
        } catch (Exception e) {
            log.error("✗ FuenteProxy ERROR: {}", e.getMessage());
            estadisticas.setHechosCargados(0L);
        }

        // 2. FuenteEstatica (hechos importados de CSV)
        try {
            Map<String, Object> stats = fuenteEstaticaClient.obtenerEstadisticas();
            if (stats != null && !stats.isEmpty()) {
                Long hechos = getLongValue(stats, "hechosImportados");
                estadisticas.setHechosImportados(hechos);
                log.info("✓ FuenteEstatica: hechosImportados={}", hechos);
            } else {
                log.warn("⚠ FuenteEstatica: respuesta vacía");
                estadisticas.setHechosImportados(0L);
            }
        } catch (Exception e) {
            log.error("✗ FuenteEstatica ERROR: {}", e.getMessage());
            estadisticas.setHechosImportados(0L);
        }

        // 3. FuenteDinamica (hechos creados/modificados y solicitudes)
        try {
            Map<String, Object> stats = fuenteDinamicaClient.obtenerEstadisticas();
            if (stats != null && !stats.isEmpty()) {
                Long hechosCreados = getLongValue(stats, "hechosCreados");
                Long hechosModificados = getLongValue(stats, "hechosModificados");

                estadisticas.setHechosCreados(hechosCreados);
                estadisticas.setHechosModificados(hechosModificados);

                // Solicitudes de creación
                Long solCreacionTotal = getLongValue(stats, "solicitudesCreacionTotal");
                solCreacionAceptadas = getLongValue(stats, "solicitudesCreacionAceptadas");
                solCreacionRechazadas = getLongValue(stats, "solicitudesCreacionRechazadas");
                solCreacionPendientes = getLongValue(stats, "solicitudesCreacionPendientes");
                solCreacionPendientesMod = getLongValue(stats, "solicitudesCreacionPendientesModificacion");

                // Solicitudes de modificación
                Long solModificacionTotal = getLongValue(stats, "solicitudesModificacionTotal");
                solModificacionAceptadas = getLongValue(stats, "solicitudesModificacionAceptadas");
                solModificacionRechazadas = getLongValue(stats, "solicitudesModificacionRechazadas");
                solModificacionPendientes = getLongValue(stats, "solicitudesModificacionPendientes");
                solModificacionPendientesMod = getLongValue(stats, "solicitudesModificacionPendientesModificacion");

                estadisticas.setSolicitudesCreacion(solCreacionTotal);
                estadisticas.setSolicitudesModificacion(solModificacionTotal);
                estadisticas.setSolicitudesPendientesModificacion(solCreacionPendientesMod + solModificacionPendientesMod);

                log.info("✓ FuenteDinamica: creados={}, modificados={}, solCreacion={}, solModificacion={}",
                        hechosCreados, hechosModificados, solCreacionTotal, solModificacionTotal);
            } else {
                log.warn("⚠ FuenteDinamica: respuesta vacía");
                estadisticas.setHechosCreados(0L);
                estadisticas.setHechosModificados(0L);
                estadisticas.setSolicitudesCreacion(0L);
                estadisticas.setSolicitudesModificacion(0L);
                estadisticas.setSolicitudesPendientesModificacion(0L);
            }
        } catch (Exception e) {
            log.error("✗ FuenteDinamica ERROR: {}", e.getMessage());
            estadisticas.setHechosCreados(0L);
            estadisticas.setHechosModificados(0L);
            estadisticas.setSolicitudesCreacion(0L);
            estadisticas.setSolicitudesModificacion(0L);
            estadisticas.setSolicitudesPendientesModificacion(0L);
        }

        // 4. Agregador (colecciones, fuentes, solicitudes de eliminación)
        try {
            Map<String, Object> stats = agregadorClient.obtenerEstadisticas();
            if (stats != null && !stats.isEmpty()) {
                Long coleccionesCreadas = getLongValue(stats, "coleccionesCreadas");
                Long coleccionesModificadas = getLongValue(stats, "coleccionesModificadas");
                Long fuentesActivas = getLongValue(stats, "fuentesActivas");

                Long solElimTotal = getLongValue(stats, "solicitudesEliminacionTotal");
                Long solElimAceptadas = getLongValue(stats, "solicitudesEliminacionAceptadas");
                Long solElimRechazadas = getLongValue(stats, "solicitudesEliminacionRechazadas");
                Long solElimPendientes = getLongValue(stats, "solicitudesEliminacionPendientes");

                estadisticas.setColeccionesCreadas(coleccionesCreadas);
                estadisticas.setColeccionesModificadas(coleccionesModificadas);
                estadisticas.setFuentesActivas(fuentesActivas);
                estadisticas.setSolicitudesEliminacion(solElimTotal);

                // Calcular TOTALES de solicitudes (suma de los 3 tipos)
                Long totalAceptadas = solElimAceptadas + solCreacionAceptadas + solModificacionAceptadas;
                Long totalRechazadas = solElimRechazadas + solCreacionRechazadas + solModificacionRechazadas;
                Long totalPendientes = solElimPendientes + solCreacionPendientes + solModificacionPendientes;

                estadisticas.setSolicitudesAceptadas(totalAceptadas);
                estadisticas.setSolicitudesRechazadas(totalRechazadas);
                estadisticas.setSolicitudesPendientes(totalPendientes);

                log.info("✓ Agregador: colecciones={}/{}, fuentes={}, solElim={}",
                        coleccionesCreadas, coleccionesModificadas, fuentesActivas, solElimTotal);
                log.info("✓ TOTALES: Aceptadas={}, Rechazadas={}, Pendientes={}",
                        totalAceptadas, totalRechazadas, totalPendientes);
            } else {
                log.warn("⚠ Agregador: respuesta vacía");
                estadisticas.setColeccionesCreadas(0L);
                estadisticas.setColeccionesModificadas(0L);
                estadisticas.setFuentesActivas(0L);
                estadisticas.setSolicitudesEliminacion(0L);
                estadisticas.setSolicitudesAceptadas(0L);
                estadisticas.setSolicitudesRechazadas(0L);
                estadisticas.setSolicitudesPendientes(0L);
            }
        } catch (Exception e) {
            log.error("✗ Agregador ERROR: {}", e.getMessage(), e);
            estadisticas.setColeccionesCreadas(0L);
            estadisticas.setColeccionesModificadas(0L);
            estadisticas.setFuentesActivas(0L);
            estadisticas.setSolicitudesEliminacion(0L);
            estadisticas.setSolicitudesAceptadas(0L);
            estadisticas.setSolicitudesRechazadas(0L);
            estadisticas.setSolicitudesPendientes(0L);
        }

        log.info("=== Estadísticas consolidadas completadas ===");
        return estadisticas;
    }

    /**
     * Extrae un valor Long de un Map de manera segura
     */
    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            log.warn("No se pudo convertir '{}' a Long para key '{}'", value, key);
            return 0L;
        }
    }

    // ========== MÉTODOS PARA EL MÓDULO DE ESTADÍSTICAS (PUERTO 8085) ==========
    // NOTA: Estos son diferentes a las estadísticas del panel de control

    public Map<String, Object> obtenerEstadisticasGenerales() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/general";
            log.info("Obteniendo estadísticas del módulo de estadísticas desde: {}", url);
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error al obtener estadísticas del módulo: {}", e.getMessage());
            return Map.of();
        }
    }

    public Map<String, Object> obtenerEstadisticasHechosProcesados() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/hechos/procesados";
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error al obtener estadísticas de hechos procesados: {}", e.getMessage());
            return Map.of();
        }
    }

    public Map<String, Object> obtenerEstadisticasSolicitudesSpam() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/solicitudes/spam";
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});

        } catch (Exception e) {
            log.error("Error al obtener estadísticas de spam: {}", e.getMessage());
            return Map.of();
        }
    }

    public Map<String, Object> obtenerEstadisticasPorProvincia() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/provincias";
            /*ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );*/
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});

            } catch (Exception e) {
            log.error("Error al obtener estadísticas por provincia: {}", e.getMessage());
            return Map.of();
        }
    }

    public Map<String, Object> obtenerEstadisticasPorCategoria() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/categorias";
            /*ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            return response.getBody();*/
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});

        } catch (Exception e) {
            log.error("Error al obtener estadísticas por categoría: {}", e.getMessage());
            return Map.of();
        }
    }

    public Map<String, Object> obtenerEstadisticasPorHora() {
        try {
            String url = estadisticasUrl + "/api/estadisticas/horas";
            /*ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            return response.getBody();*/
            return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});

        } catch (Exception e) {
            log.error("Error al obtener estadísticas por hora: {}", e.getMessage());
            return Map.of();
        }
    }
}

