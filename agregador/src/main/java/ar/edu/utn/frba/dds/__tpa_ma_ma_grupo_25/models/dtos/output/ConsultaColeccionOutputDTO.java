package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.util.List;

@Data
public class ConsultaColeccionOutputDTO {
  private final ColeccionOutputDTO info;
  private final List<HechoOutputDTO> hechos;

  public ConsultaColeccionOutputDTO(ColeccionOutputDTO info, List<HechoOutputDTO> hechos) {
    this.info = info;
    this.hechos = hechos;
  }
}
