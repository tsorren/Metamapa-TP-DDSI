package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionDTO {
  private String handle;
  private String titulo;
  private String descripcion;
  private List<FuenteDTO> fuentes;
  @JsonProperty("CriterioDePertenencia")
  private CriterioDePertenenciaDTO criterioDePertenencia;
  private TipoConsenso algoritmoConsenso;
}
