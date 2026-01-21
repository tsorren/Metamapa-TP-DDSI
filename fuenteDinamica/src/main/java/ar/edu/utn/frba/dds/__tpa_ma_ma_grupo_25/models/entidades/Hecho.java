package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hechos")
public class Hecho {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  @ManyToOne
  @JoinColumn(name = "categoria_id", referencedColumnName = "id")
  private Categoria categoria;

  @Embedded
  private Ubicacion ubicacion;

  @Column(name = "fecha_del_hecho")
  private LocalDate fechaDelHecho;

  @Column(name = "fecha_de_carga")
  private LocalDateTime fechaDeCarga;

  @Column(name = "fecha_ultima_modificacion")
  private LocalDateTime fechaUltimaModificacion;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "multimedia_id", referencedColumnName = "id")
  private Multimedia contenido;

  @Enumerated(EnumType.STRING)
  @Column(name = "origen")
  private TipoOrigen origen;

  // ✅ ManyToMany corregido con tabla intermedia
  @ManyToMany
  @JoinTable(
          name = "hecho_etiqueta",
          joinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "etiqueta_id", referencedColumnName = "id")
  )
  private List<Etiqueta> etiquetas;

  // ✅ Bidireccional con FuenteDinamica
  @ManyToMany(mappedBy = "hechosAsociados")
  private List<FuenteDinamica> fuentes;

  @Column(name = "visible")
  private Boolean visible;

  // ---------- Métodos de ayuda ----------
  public static Hecho crear(String titulo, String descripcion, Categoria categoria,
                            Ubicacion ubicacion, LocalDate fecha, TipoOrigen origen, Multimedia contenido) {
    return Hecho.builder()
            .titulo(titulo)
            .descripcion(descripcion)
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(fecha)
            .fechaDeCarga(LocalDateTime.now())
            .fechaUltimaModificacion(LocalDateTime.now())
            .origen(origen)
            .contenido(contenido)
            .etiquetas(new ArrayList<>())
            .visible(true)
            .build();
  }

  public void agregarEtiqueta(Etiqueta nuevaEtiqueta) {
    this.etiquetas.add(nuevaEtiqueta);
  }

  public HechoOutputDTO toDTO() {
    return new HechoOutputDTO(
            id,
            titulo,
            descripcion,
            categoria != null ? categoria.toDTO() : null,
            ubicacion != null ? ubicacion.toDTO() : null,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            contenido != null ? contenido.toDTO() : null,
            origen,
            etiquetas != null ? etiquetas.stream().map(Etiqueta::toDTO).toList() : null,
            visible
    );
  }
}
