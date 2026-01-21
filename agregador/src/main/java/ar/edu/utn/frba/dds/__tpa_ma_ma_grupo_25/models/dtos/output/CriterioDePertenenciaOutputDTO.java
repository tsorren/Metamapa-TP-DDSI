package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.util.List;

@Data
public class CriterioDePertenenciaOutputDTO {
  private final Long identificador;
  private final List<FiltroOutputDTO> filtros;
}
