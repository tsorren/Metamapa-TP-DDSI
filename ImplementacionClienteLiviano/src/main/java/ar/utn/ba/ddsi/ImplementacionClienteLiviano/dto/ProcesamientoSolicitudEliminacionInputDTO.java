package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcesamientoSolicitudEliminacionInputDTO {
  private Long idResponsable;
  private EstadoAtencionSolicitud estado;
  private String justificacion;
}
