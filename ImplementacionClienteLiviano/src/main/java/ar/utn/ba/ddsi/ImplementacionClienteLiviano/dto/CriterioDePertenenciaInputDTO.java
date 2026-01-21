package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriterioDePertenenciaInputDTO {
  private Long idFiltro;
  private Accion accion;
}
