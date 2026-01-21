package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudDeEliminacionOutputDTO {
  private final Long id;
  private final LocalDateTime fechaCreacion;
  private final EstadoAtencionSolicitud estadoAtencionSolicitud;
  private final HechoOutputDTO hecho;
  private final String justificacion;
  private final ContribuyenteOutputDTO contribuyente;
  private final AdministradorOutputDTO administradorResponsable;
  private final LocalDateTime fechaResolucion;
  private final String justificacionRechazo;
}
