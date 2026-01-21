package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoConsenso;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionInputDTO {
  private final Long identificador;
  private final String handle;
  private final String titulo;
  private final String descripcion;
  private final List<FuenteInputDTO> fuentes;
  private final CriterioDePertenenciaInputDTO CriterioDePertenencia;
  private final TipoConsenso algoritmoConsenso;
}
