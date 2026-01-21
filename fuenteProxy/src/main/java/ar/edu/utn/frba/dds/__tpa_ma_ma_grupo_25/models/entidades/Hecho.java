package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hecho {
  private Long identificador;
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Ubicacion ubicacion;
  private LocalDate fechaDelHecho;
  private LocalDateTime fechaDeCarga;
  private Multimedia contenido;
  private TipoOrigen origen; // TODO: Definir tipo de dato: Origen o TipoOrigen
  private Set<Etiqueta> etiquetas;
  private Boolean visible;


  public static Hecho crear(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fecha, TipoOrigen origen) {
    return Hecho
            .builder()
            .identificador(null)
            .titulo(titulo)
            .descripcion(descripcion)
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(fecha)
            .fechaDeCarga(LocalDateTime.now())
            .origen(origen)
            .contenido(null)
            .etiquetas(new HashSet<>())
            .visible(true)
            .build();
  }

  public void ocultar() {
    this.setVisible(false);
  }

  public void agregarEtiqueta(Etiqueta nuevaEtiqueta) {
    this.etiquetas.add(nuevaEtiqueta);
  }

}
