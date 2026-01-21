package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.NotificacionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.NotificacionService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

  private final NotificacionService notificacionService;

  public NotificacionController(NotificacionService notificacionService) {
    this.notificacionService = notificacionService;
  }

  /**
   * Endpoint para obtener notificaciones del usuario actual
   */
  @GetMapping("/mis-notificaciones")
  @ResponseBody
  public ResponseEntity<List<NotificacionDTO>> obtenerMisNotificaciones(HttpSession session) {
    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
      return ResponseEntity.status(401).build();
    }

    List<NotificacionDTO> notificaciones;
    if ("ADMIN".equals(rol)) {
      notificaciones = notificacionService.obtenerNotificacionesDeAdministrador(userId);
    } else {
      notificaciones = notificacionService.obtenerNotificacionesDeContribuyente(userId);
    }

    return ResponseEntity.ok(notificaciones);
  }

  /**
   * Endpoint para obtener solo las notificaciones no leídas (para el badge)
   */
  @GetMapping("/no-leidas")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerNotificacionesNoLeidas(HttpSession session) {
    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    // Log comentado para producción
    // log.info("[Notificaciones] GET /no-leidas - userId: {}, rol: {}", userId, rol);

    if (userId == null) {
      log.warn("Acceso no autorizado a notificaciones - sin userId en sesión");
      return ResponseEntity.status(401).build();
    }

    List<NotificacionDTO> notificaciones;
    if ("ADMIN".equals(rol)) {
      // log.info("[Notificaciones] Cargando notificaciones no leídas para ADMIN id: {}", userId);
      notificaciones = notificacionService.obtenerNotificacionesNoLeidasDeAdministrador(userId);
    } else {
      // log.info("[Notificaciones] Cargando notificaciones no leídas para CONTRIBUYENTE id: {}", userId);
      notificaciones = notificacionService.obtenerNotificacionesNoLeidasDeContribuyente(userId);
    }

    // log.info("[Notificaciones] Se encontraron {} notificaciones no leídas", notificaciones.size());

    Map<String, Object> response = new HashMap<>();
    response.put("count", notificaciones.size());
    response.put("notificaciones", notificaciones);

    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para marcar una notificación como leída
   */
  @PatchMapping("/{id}/marcar-leida")
  @ResponseBody
  public ResponseEntity<Void> marcarComoLeida(
      @PathVariable Long id,
      @RequestParam(required = false) String tipoSolicitud) {
    notificacionService.marcarComoLeida(id, tipoSolicitud);
    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint para obtener el contador de notificaciones no leídas (solo el número)
   */
  @GetMapping("/contador")
  @ResponseBody
  public ResponseEntity<Map<String, Integer>> obtenerContadorNotificaciones(HttpSession session) {
    // Log comentado para producción
    // log.info("[Notificaciones] GET /contador - SessionId: {}", session.getId());

    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    // log.info("[Notificaciones] Sesión - userId: {}, rol: {}", userId, rol);

    if (userId == null) {
      log.warn("Acceso no autorizado a contador - sin userId. Attrs: {}",
               java.util.Collections.list(session.getAttributeNames()));
      return ResponseEntity.status(401).build();
    }

    int count;
    if ("ADMIN".equals(rol)) {
      // log.info("[Notificaciones] Obteniendo contador para ADMIN id: {}", userId);
      count = notificacionService.contarNotificacionesNoLeidasDeAdministrador(userId);
    } else {
      // log.info("[Notificaciones] Obteniendo contador para CONTRIBUYENTE id: {}", userId);
      count = notificacionService.contarNotificacionesNoLeidasDeContribuyente(userId);
    }

    // log.info("[Notificaciones] Contador: {} notificaciones no leídas", count);

    Map<String, Integer> response = new HashMap<>();
    response.put("count", count);

    return ResponseEntity.ok(response);
  }

  /**
   * Vista de todas las notificaciones
   */
  @GetMapping("/todas")
  public String vistaTodasNotificaciones() {
    return "todasNotificaciones";
  }

  /**
   * Endpoint JSON para todas las notificaciones
   */
  @GetMapping("/todas-json")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerTodasNotificacionesJson(HttpSession session) {
    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
      return ResponseEntity.status(401).build();
    }

    List<NotificacionDTO> notificaciones;
    if ("ADMIN".equals(rol)) {
      notificaciones = notificacionService.obtenerNotificacionesDeAdministrador(userId);
    } else {
      notificaciones = notificacionService.obtenerNotificacionesDeContribuyente(userId);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("notificaciones", notificaciones);

    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para obtener solo las notificaciones leídas
   */
  @GetMapping("/leidas")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerNotificacionesLeidas(HttpSession session) {
    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
      return ResponseEntity.status(401).build();
    }

    List<NotificacionDTO> notificaciones;
    if ("ADMIN".equals(rol)) {
      notificaciones = notificacionService.obtenerNotificacionesLeidasDeAdministrador(userId);
    } else {
      notificaciones = notificacionService.obtenerNotificacionesLeidasDeContribuyente(userId);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("notificaciones", notificaciones);

    return ResponseEntity.ok(response);
  }

  /**
   * Endpoint para marcar todas las notificaciones como leídas
   */
  @PatchMapping("/marcar-todas-leidas")
  @ResponseBody
  public ResponseEntity<Void> marcarTodasComoLeidas(HttpSession session) {
    String rol = (String) session.getAttribute("rol");
    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
      return ResponseEntity.status(401).build();
    }

    if ("ADMIN".equals(rol)) {
      notificacionService.marcarTodasComoLeidasAdministrador(userId);
    } else {
      notificacionService.marcarTodasComoLeidasContribuyente(userId);
    }

    return ResponseEntity.ok().build();
  }
}

