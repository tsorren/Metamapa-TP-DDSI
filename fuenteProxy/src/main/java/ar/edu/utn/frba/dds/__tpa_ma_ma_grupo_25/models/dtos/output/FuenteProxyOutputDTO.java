package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FuenteProxyOutputDTO {
  private final Long identificador;
  private final LocalDateTime fechaDeCreacion;  // ✅ Cambiado de fechaCreacion a fechaDeCreacion
  // private final String url;
}
