package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;
;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoContenido;
import lombok.Data;

@Data
public class MultimediaOutputDTO {
  private final Long id;
  private final TipoContenido tipoContenido;
  private final String url;
}
