package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import lombok.Data;

@Data
public class ModuloFuenteInputDTO {
  private TipoFuente tipo;
  private String url;
}
