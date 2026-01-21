package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

@Data
public class CriterioDePertenenciaDTO {
  private Long identificador;
  private List<FiltroOutputDTO> filtros;
}
