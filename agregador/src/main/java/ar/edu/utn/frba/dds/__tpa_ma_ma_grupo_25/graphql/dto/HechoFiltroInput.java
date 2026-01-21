package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.graphql.dto;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class HechoFiltroInput {
  private String pais;
  private String provincia;
  private String ciudad;
  private Boolean visible;
  private TipoOrigen origen;
  private LocalDate fechaHasta;
  private LocalDate fechaDesde;
  private List<String> etiquetas;
  private Long categoriaId;
  private Long fuenteId;
}



