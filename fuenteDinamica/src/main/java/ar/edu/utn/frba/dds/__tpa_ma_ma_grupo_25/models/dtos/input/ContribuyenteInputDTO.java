package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class ContribuyenteInputDTO {
  private final Long id;
  private final String nombre;
  private final String apellido;
  private final Integer edad;
}

