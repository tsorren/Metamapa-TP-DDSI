package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class ColeccionInputDTO {
  private String titulo;
  private String descripcion;
  private long fuenteId;
  private long criterioDePertenenciaID;
}
