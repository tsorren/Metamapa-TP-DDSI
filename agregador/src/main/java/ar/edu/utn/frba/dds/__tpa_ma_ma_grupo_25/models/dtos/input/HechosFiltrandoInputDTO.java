package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HechosFiltrandoInputDTO {
  private final Categoria categoria;
  private final TipoOrigen tipoOrigen;
  private final LocalDateTime fecha_reporte_desde;
  private final LocalDateTime fecha_reporte_hasta;
  private final LocalDate fecha_acontecimiento_desde;
  private final LocalDate fecha_acontecimiento_hasta;
  private final Ubicacion ubicacion;
  private final Double radio;
}