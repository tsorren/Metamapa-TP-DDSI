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
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesDeAdministrador(idAdministrador);
    log.debug("Notificaciones para admin {}: {}", idAdministrador, notificaciones.size());
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/contribuyente/{idContribuyente}")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesDeContribuyente(
          @PathVariable Long idContribuyente) {
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesDeContribuyente(idContribuyente);
    log.debug("Notificaciones para contribuyente {}: {}", idContribuyente, notificaciones.size());
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/administrador/{idAdministrador}/no-leidas")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesNoLeidasDeAdministrador(
          @PathVariable Long idAdministrador) {
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador);
    log.debug("Notificaciones no leidas para admin {}: {}", idAdministrador, notificaciones.size());
    return ResponseEntity.ok(notificaciones);
  }

  @GetMapping("/contribuyente/{idContribuyente}/no-leidas")
  public ResponseEntity<List<NotificacionOutputDTO>> obtenerNotificacionesNoLeidasDeContribuyente(
          @PathVariable Long idContribuyente) {
    List<NotificacionOutputDTO> notificaciones = notificacionServicio.obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente);
    log.debug("Notificaciones no leidas para contribuyente {}: {}", idContribuyente, notificaciones.size());
    return ResponseEntity.ok(notificaciones);
  }

  @PatchMapping("/{idNotificacion}/marcar-leida")
  public ResponseEntity<Void> marcarComoLeida(@PathVariable Long idNotificacion) {
    notificacionServicio.marcarComoLeida(idNotificacion);
    log.info("Notificacion {} marcada como leida", idNotificacion);
    return ResponseEntity.ok().build();
  }
}
