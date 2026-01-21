package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

@Data
public class FuenteOutputDTO {
  private final Long identificador;
  private final Long identificadorExterno;
  private final ModuloFuenteOutputDTO moduloFuente;
}
