package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Multimedia {
  private Long identificador;
  private TipoContenido tipoContenido;
  private String url;
}
