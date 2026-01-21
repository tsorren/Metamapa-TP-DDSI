package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class FuenteInputDTO {
  private final Long identificador;
  private final Long identificadorExterno;
  private final ModuloFuenteInputDTO moduloFuente;
}
