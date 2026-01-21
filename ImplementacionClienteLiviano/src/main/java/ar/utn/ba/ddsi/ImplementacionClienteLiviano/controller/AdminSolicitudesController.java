package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.SolicitudesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/solicitudes")
public class AdminSolicitudesController {

  private final SolicitudesService solicitudesService;

  public AdminSolicitudesController(SolicitudesService solicitudesService) {
    this.solicitudesService = solicitudesService;
  }

  /**
   * Renderizar la página de solicitudes
   */
  @GetMapping
  public String adminSolicitudes(Model model) {
    try {
      // La carga de datos se hará por AJAX en el frontend
      return "solicitudes";
    } catch (Exception e) {
      log.error("Error al cargar página de solicitudes: {}", e.getMessage());
      model.addAttribute("error", "Error al cargar las solicitudes");
      return "solicitudes";
    }
  }

  /**
   * API: Obtener todas las solicitudes pendientes
   */
  @GetMapping("/pendientes")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerSolicitudesPendientes() {
    try {
      Map<String, Object> solicitudes = solicitudesService.obtenerTodasLasSolicitudesPendientes();
      return ResponseEntity.ok(solicitudes);
    } catch (Exception e) {
      log.error("Error al obtener solicitudes pendientes: {}", e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("error", "Error al cargar las solicitudes: " + e.getMessage());
      return ResponseEntity.internalServerError().body(error);
    }
  }

  /**
   * API: Procesar solicitud de eliminación
   */
  @PatchMapping("/eliminacion/{idSolicitud}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> procesarSolicitudEliminacion(
          @PathVariable Long idSolicitud,
          @RequestBody ProcesamientoSolicitudEliminacionInputDTO procesamiento,
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      procesamiento.setIdResponsable(userDetails.getId());
      SolicitudDeEliminacionOutputDTO resultado = solicitudesService.procesarSolicitudEliminacion(idSolicitud, procesamiento);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Solicitud de eliminación procesada correctamente");
      response.put("solicitud", resultado);
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de eliminación {}: {}", idSolicitud, e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al procesar la solicitud: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  /**
   * API: Procesar solicitud de creación
   */
  @PatchMapping("/creacion/{idSolicitud}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> procesarSolicitudCreacion(
          @PathVariable Long idSolicitud,
          @RequestBody ProcesamientoSolicitudRevisionInputDTO procesamiento,
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      procesamiento.setIdResponsable(userDetails.getId());
      SolicitudCreacionOutputDTO resultado = solicitudesService.procesarSolicitudCreacion(idSolicitud, procesamiento);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Solicitud de creación procesada correctamente");
      response.put("solicitud", resultado);
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de creación {}: {}", idSolicitud, e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al procesar la solicitud: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }

  /**
   * API: Procesar solicitud de modificación
   */
  @PatchMapping("/modificacion/{idSolicitud}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> procesarSolicitudModificacion(
          @PathVariable Long idSolicitud,
          @RequestBody ProcesamientoSolicitudRevisionInputDTO procesamiento,
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      procesamiento.setIdResponsable(userDetails.getId());
      SolicitudDeModificacionOutputDTO resultado = solicitudesService.procesarSolicitudModificacion(idSolicitud, procesamiento);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Solicitud de modificación procesada correctamente");
      response.put("solicitud", resultado);
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de modificación {}: {}", idSolicitud, e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al procesar la solicitud: " + e.getMessage());
      return ResponseEntity.badRequest().body(error);
    }
  }
}

