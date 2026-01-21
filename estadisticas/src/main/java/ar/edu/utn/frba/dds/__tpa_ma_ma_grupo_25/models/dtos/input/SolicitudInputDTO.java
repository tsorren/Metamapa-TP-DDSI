package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.EstadoAtencionSolicitud;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudInputDTO {
  private final Long identificador;
  private final EstadoAtencionSolicitud estadoAtencionSolicitud;
  private final HechoInputDTO hecho;
  private final String justificacion;
  private final Contribuyente contribuyente;
  private final LocalDateTime fechaCreacion;
  private final Administrador administradorResponsable;
  private final LocalDateTime fechaResolucion;
}
