package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultarColeccionDTO {
  private ColeccionDTO info;
  private List<HechoDTO> hechos;
  private Integer paginaActual;
  private Integer totalPaginas;
  private Long totalElementos;
  private Integer elementosPorPagina;
}
