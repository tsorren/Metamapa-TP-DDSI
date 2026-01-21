package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import lombok.Data;

@Data
public class ProcesamientoSolicitudInputDTO {
  private Long idResponsable;
  private EstadoAtencionSolicitud estado;
  private String justificacion;
}
