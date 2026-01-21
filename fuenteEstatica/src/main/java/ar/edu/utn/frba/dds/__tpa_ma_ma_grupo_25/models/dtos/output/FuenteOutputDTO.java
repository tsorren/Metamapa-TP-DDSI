package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FuenteOutputDTO {
  private final Long identificador;
  private final LocalDateTime fechaDeCreacion;
  //private final Path ruta;
}
