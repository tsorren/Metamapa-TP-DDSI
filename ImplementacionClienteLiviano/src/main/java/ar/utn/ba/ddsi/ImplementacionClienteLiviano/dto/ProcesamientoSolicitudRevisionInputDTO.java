package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesamientoSolicitudRevisionInputDTO {
  private Long idResponsable;
  private TipoEstadoRevision nuevoEstado;
  private String feedback;
}
