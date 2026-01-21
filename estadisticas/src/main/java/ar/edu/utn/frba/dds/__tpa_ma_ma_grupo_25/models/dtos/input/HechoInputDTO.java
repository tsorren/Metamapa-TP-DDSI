package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Etiqueta;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoOrigen;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class HechoInputDTO {
  private final Long identificador;
  private final Long identificadorExterno;
  private final FuenteInputDTO fuente;
  private final String titulo;
  private final String descripcion;
  private final Categoria categoria; // TODO: Pasar a categoríaId?
  private final Ubicacion ubicacion;
  private final LocalDate fechaDelHecho;
  private final LocalDateTime fechaDeCarga;
  private final LocalDateTime fechaUltimaModificacion;
  private final Multimedia contenido;
  private final TipoOrigen origen; // TODO: Definir tipo de dato: Origen o TipoOrigen
  private final List<Etiqueta> etiquetas;
  private final Boolean visible;
}
