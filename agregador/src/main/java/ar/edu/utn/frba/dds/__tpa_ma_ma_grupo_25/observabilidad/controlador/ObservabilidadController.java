package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.controlador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.dto.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.servicio.ObservabilidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observabilidad")
@CrossOrigin(origins = "*")
public class ObservabilidadController {

    private static final Logger logger = LoggerFactory.getLogger(ObservabilidadController.class);

    @Autowired
    private ObservabilidadService observabilidadService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        try {
            logger.debug("Solicitando dashboard de observabilidad");
            DashboardDTO dashboard = observabilidadService.getDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error obteniendo dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<List<ServiceHealthDTO>> getServicesHealth() {
        try {
            logger.debug("Verificando salud de servicios");
            List<ServiceHealthDTO> health = observabilidadService.checkServicesHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            logger.error("Error verificando salud de servicios: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<ServiceMetricsDTO>> getServicesMetrics() {
        try {
            logger.debug("Obteniendo metricas de servicios");
            List<ServiceMetricsDTO> metrics = observabilidadService.getServicesMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            logger.error("Error obteniendo metricas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<LogEntryDTO>> getRecentLogs(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            logger.debug("Obteniendo logs recientes (limit: {})", limit);
            List<LogEntryDTO> logs = observabilidadService.getRecentLogs(limit);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Error obteniendo logs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<SystemSummaryDTO> getSystemSummary() {
        try {
            logger.debug("Obteniendo resumen del sistema");
            List<ServiceHealthDTO> health = observabilidadService.checkServicesHealth();
            List<ServiceMetricsDTO> metrics = observabilidadService.getServicesMetrics();
            SystemSummaryDTO summary = observabilidadService.getSystemSummary(health, metrics);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error obteniendo resumen: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

