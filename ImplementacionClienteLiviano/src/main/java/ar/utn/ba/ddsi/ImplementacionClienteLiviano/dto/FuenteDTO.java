package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

@Data
public class FuenteDTO {
  private Long identificador;
  private Long identificadorExterno;
  private ModuloFuenteDTO moduloFuente;
}
