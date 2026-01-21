package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

@Data
public class CriterioDePertenenciaOutputDTO {
  private Long identificador;
  private List<FiltroOutputDTO> filtros;
}
