package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import lombok.Data;

@Data
public class SolicitudProcesamientoInputDTO {
  private Long idResponsable;
  private TipoEstadoRevision nuevoEstado;
  private String feedback;
}
