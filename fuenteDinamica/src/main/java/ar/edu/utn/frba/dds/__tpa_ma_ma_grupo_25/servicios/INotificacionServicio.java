package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.NotificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;

import java.util.List;

public interface INotificacionServicio {
  // Crear notificaciones para todos los administradores
  void notificarATodosLosAdministradores(String mensaje, TipoNotificacion tipo, Long solicitudId, TipoSolicitud tipoSolicitud);

  // Crear notificación para un contribuyente específico
  void notificarAContribuyente(Long idContribuyente, String mensaje, TipoNotificacion tipo, Long solicitudId, TipoSolicitud tipoSolicitud);

  // Obtener notificaciones de un administrador
  List<NotificacionOutputDTO> obtenerNotificacionesDeAdministrador(Long idAdministrador);

  // Obtener notificaciones de un contribuyente
  List<NotificacionOutputDTO> obtenerNotificacionesDeContribuyente(Long idContribuyente);

  // Marcar notificación como leída
  void marcarComoLeida(Long idNotificacion);

  // Obtener notificaciones no leídas
  List<NotificacionOutputDTO> obtenerNotificacionesNoLeidasDeAdministrador(Long idAdministrador);
  List<NotificacionOutputDTO> obtenerNotificacionesNoLeidasDeContribuyente(Long idContribuyente);
}

