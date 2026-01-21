package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import lombok.Data;

@Data
public class ModuloFuenteOutputDTO {
  private final Long identificador;
  private final String url;
  private final TipoFuente tipo;
}
