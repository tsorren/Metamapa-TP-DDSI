package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

@Data
public class ColeccionInputDTO {
  private String titulo;
  private String descripcion;
  private Long criterioDePertenenciaID;
  private List<Long> idsFuentes;
  private TipoConsenso algoritmoConsenso;
}
