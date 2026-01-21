package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionOutputDTO {
  private final String handle;
  private final String titulo;
  private final String descripcion;
  private final List<FuenteOutputDTO> fuentes;
  private final CriterioDePertenenciaOutputDTO CriterioDePertenencia;
  private final TipoConsenso algoritmoConsenso;
}
