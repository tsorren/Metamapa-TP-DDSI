package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para servir datos de observabilidad
 * Actúa como proxy hacia el agregador para evitar problemas de CORS
 */
@Slf4j
@RestController
@RequestMapping("/api/observabilidad")
public class ObservabilidadDataController {

    private final WebClient webClient;
    private final String agregadorUrl;

    public ObservabilidadDataController(@Value("${agregador.url}") String agregadorUrl) {
        this.agregadorUrl = agregadorUrl;
        this.webClient = WebClient.builder().baseUrl(agregadorUrl).build();
    }

    /**
     * Obtiene el dashboard completo de observabilidad
     * Solo accesible para ADMIN
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboard() {
        try {
            log.info("Solicitando dashboard de observabilidad desde agregador");

            // Hacer petición al agregador
            Object dashboard = webClient.get()
                    .uri("/api/observabilidad/dashboard")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error obteniendo dashboard desde agregador: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener datos de observabilidad");
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene el estado de salud de los servicios
     */
    @GetMapping("/health")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getServicesHealth() {
        try {
            log.info("Solicitando estado de salud de servicios desde agregador");

            Object health = webClient.get()
                    .uri("/api/observabilidad/health")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error obteniendo salud de servicios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene métricas de los servicios
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getServicesMetrics() {
        try {
            log.info("Solicitando métricas de servicios desde agregador");

            Object metrics = webClient.get()
                    .uri("/api/observabilidad/metrics")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error obteniendo métricas de servicios: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene los logs recientes
     */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecentLogs() {
        try {
            log.info("Solicitando logs recientes desde agregador");

            Object logs = webClient.get()
                    .uri("/api/observabilidad/logs?limit=50")
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error obteniendo logs recientes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

