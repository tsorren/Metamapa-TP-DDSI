package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class NavegacionInputDTO {
  private final String handle;
  private final Boolean navegacionCurada;
  private final HechosFiltrandoInputDTO filtros;
}


