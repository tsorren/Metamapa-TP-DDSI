package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

@Data
public class FiltroDTO {
  private Long identificador;
  private String tipoFiltro;
  private List<String> parametros;
}