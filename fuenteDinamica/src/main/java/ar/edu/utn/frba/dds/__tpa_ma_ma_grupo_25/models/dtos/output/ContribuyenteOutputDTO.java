package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.util.List;

@Data
public class ContribuyenteOutputDTO {
  private final Long id;
  private final String nombre;
  private final String apellido;
  private final Integer edad;
}
