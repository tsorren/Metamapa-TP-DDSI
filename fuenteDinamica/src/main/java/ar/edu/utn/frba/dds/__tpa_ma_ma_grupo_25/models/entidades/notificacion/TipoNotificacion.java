package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion;

public enum TipoNotificacion {
  SOLICITUD_PENDIENTE,  // Para administradores cuando se crea una solicitud
  SOLICITUD_PROCESADA,  // Para contribuyentes cuando su solicitud es procesada
  SOLICITUD_ACEPTADA,
  SOLICITUD_RECHAZADA,
  SOLICITUD_PENDIENTE_MODIFICACION
}

