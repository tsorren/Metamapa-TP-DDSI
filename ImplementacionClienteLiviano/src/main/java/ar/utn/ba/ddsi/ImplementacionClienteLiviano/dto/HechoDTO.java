package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HechoDTO {
  private Long identificador;
  private Long identificadorExterno;
  private FuenteDTO fuente;
  private String titulo;
  private String descripcion;
  private Categoria categoria; // placeholder class
  private Ubicacion ubicacion; // placeholder
  private LocalDate fechaDelHecho;
  private LocalDateTime fechaDeCarga;
  private LocalDateTime fechaUltimaModificacion;
  private Multimedia contenido; // placeholder
  private TipoOrigen origen; // placeholder
  private List<EtiquetaDTO> etiquetas; // simplified etiqueta
  private Boolean visible;
}
