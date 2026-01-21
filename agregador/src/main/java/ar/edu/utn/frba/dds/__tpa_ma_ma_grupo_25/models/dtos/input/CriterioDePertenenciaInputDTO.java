package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Accion;
import lombok.Data;

@Data
public class CriterioDePertenenciaInputDTO {
  private final Long idFiltro;
  private final Accion accion;
}
