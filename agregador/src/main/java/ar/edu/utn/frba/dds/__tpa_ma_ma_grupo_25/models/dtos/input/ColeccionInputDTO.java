package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionInputDTO {
  private final String titulo;
  private final String descripcion;
  private final Long criterioDePertenenciaID;
  private final List<Long> idsFuentes;
  private final TipoConsenso algoritmoConsenso;
}
