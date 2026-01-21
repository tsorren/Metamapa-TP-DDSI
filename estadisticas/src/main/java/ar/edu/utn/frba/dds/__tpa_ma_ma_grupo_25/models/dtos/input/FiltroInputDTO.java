package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

import java.util.List;

@Data
public class FiltroInputDTO {
  private final Long identificador;
  private final String tipoFiltro;
  private final List<String> parametros;
}
