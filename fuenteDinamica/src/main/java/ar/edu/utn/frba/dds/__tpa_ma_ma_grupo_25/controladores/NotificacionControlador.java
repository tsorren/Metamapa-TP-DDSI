package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.NotificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.INotificacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notificaciones")
public class NotificacionControlador {

  private final INotificacionServicio notificacionServicio;

  public NotificacionControlador(INotificacionServicio notificacionServicio) {
    this.notificacionServicio = notificacionServicio;
  }

  @GetMapping("/administrador/{idAdministrador}")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesDeAdministrador(
          @PathVariable Long idAdministrador) {
    //log.info("[FuenteDinamica] GET /notificaciones/administrador/{} - Obteniendo todas las notificaciones", idAdministrador);
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesDeAdministrador(idAdministrador);
    log.info("[FuenteDinamica] Encontradas {} notificaciones para admin {}", notificaciones.size(), idAdministrador);
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/contribuyente/{idContribuyente}")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesDeContribuyente(
          @PathVariable Long idContribuyente) {
    //log.info("[FuenteDinamica] GET /notificaciones/contribuyente/{} - Obteniendo todas las notificaciones", idContribuyente);
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesDeContribuyente(idContribuyente);
    log.info("[FuenteDinamica] Encontradas {} notificaciones para contribuyente {}", notificaciones.size(), idContribuyente);
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/administrador/{idAdministrador}/no-leidas")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesNoLeidasDeAdministrador(
          @PathVariable Long idAdministrador) {
    //log.info("[FuenteDinamica] GET /notificaciones/administrador/{}/no-leidas", idAdministrador);
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador);
    log.info("[FuenteDinamica] Encontradas {} notificaciones NO LEÍDAS para admin {}", notificaciones.size(), idAdministrador);
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/contribuyente/{idContribuyente}/no-leidas")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesNoLeidasDeContribuyente(
          @PathVariable Long idContribuyente) {
    //log.info("[FuenteDinamica] GET /notificaciones/contribuyente/{}/no-leidas", idContribuyente);
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente);
    log.info("[FuenteDinamica] Encontradas {} notificaciones NO LEÍDAS para contribuyente {}", notificaciones.size(), idContribuyente);
    return ResponseEntity.ok(notificaciones);
  }

  @PatchMapping("/{idNotificacion}/marcar-leida")
  public ResponseEntity<Void> marcarComoLeida(@PathVariable Long idNotificacion) {
    //log.info("[FuenteDinamica] PATCH /notificaciones/{}/marcar-leida", idNotificacion);
    notificacionServicio.marcarComoLeida(idNotificacion);
    log.info("[FuenteDinamica] Notificación {} marcada como leída", idNotificacion);
    return ResponseEntity.ok().build();
  }
}

