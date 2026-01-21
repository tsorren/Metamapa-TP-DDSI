package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

@Data
public class ContribuyenteDinamicaOutputDTO {
  private final Long id;
  private final String nombre;
  private final String apellido;
  private final Integer edad;
}
