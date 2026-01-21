package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FuenteEstaticaInputDTO {
  private final LocalDateTime fechaCreacio;
  private final String path;
}
