package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

@Data
public class ContribuyenteSimple {
  private Long identificador;
  private String nombre;
  private String apellido;
  private Integer edad;
}