package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.HistorialSolicitudesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/historial-solicitudes")
@PreAuthorize("hasRole('ADMIN')")
public class HistorialSolicitudesController {

  private final HistorialSolicitudesService historialSolicitudesService;

  public HistorialSolicitudesController(HistorialSolicitudesService historialSolicitudesService) {
    this.historialSolicitudesService = historialSolicitudesService;
  }

  /**
   * Página de historial de solicitudes
   */
  @GetMapping
  public String mostrarHistorial() {
    return "historialSolicitudes";
  }

  /**
   * API: Obtener historial completo de todas las solicitudes
   */
  @GetMapping("/api/completo")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerHistorialCompleto() {
    try {
      log.info("Obteniendo historial completo de solicitudes");
      Map<String, Object> historial = historialSolicitudesService.obtenerHistorialCompleto();
      log.info("Historial obtenido exitosamente: {} tipos de solicitudes", historial.size());
      return ResponseEntity.ok(historial);
    } catch (Exception e) {
      log.error("Error al obtener historial completo: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * API: Obtener historial de un tipo específico de solicitud
   */
  @GetMapping("/api/tipo/{tipo}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerHistorialPorTipo(@PathVariable String tipo) {
    try {
      log.info("Obteniendo historial del tipo: {}", tipo);
      Map<String, Object> historial = historialSolicitudesService.obtenerHistorialPorTipo(tipo);
      return ResponseEntity.ok(historial);
    } catch (IllegalArgumentException e) {
      log.error("Tipo de solicitud no válido: {}", tipo);
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      log.error("Error al obtener historial del tipo {}: {}", tipo, e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }
}

