package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

@Data
public class FiltroInputDTO {
  private String tipoFiltro;
  private List<String> parametros;
}