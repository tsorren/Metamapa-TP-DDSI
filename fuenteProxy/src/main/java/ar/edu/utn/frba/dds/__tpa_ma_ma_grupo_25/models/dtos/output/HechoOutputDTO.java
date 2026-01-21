package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Etiqueta;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@Data
public class HechoOutputDTO {
  private final Long identificador;
  private final String titulo;
  private final String descripcion;
  private final Categoria categoria; // TODO: Pasar a categoríaId?
  private final Ubicacion ubicacion;
  private final LocalDate fechaDelHecho;
  private final LocalDateTime fechaDeCarga;
  private final Multimedia contenido;
  private final TipoOrigen origen; // TODO: Definir tipo de dato: Origen o TipoOrigen
  private final Set<Etiqueta> etiquetas;
  private final Boolean visible;
}
