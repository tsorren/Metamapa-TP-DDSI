package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.metamapa;

import lombok.Data;

import java.util.List;

@Data
public class HechosMetamapaInput {
  private List<HechoMetamapaInputDTO> hechos;
}
