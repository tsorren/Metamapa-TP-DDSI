package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hechos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, name = "idExterno", columnDefinition = "LONG")
  private Long idExterno; // Identificador que provee la fuente

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fuente_id", referencedColumnName = "id", nullable = false)
  private Fuente fuente;

  @Column(name = "titulo", columnDefinition = "VARCHAR(255)")
  private String titulo;

  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", referencedColumnName = "id")
  private Categoria categoria;

  @Embedded
  private Ubicacion ubicacion;

  @Column(name = "fecha_del_hecho")
  private LocalDate fechaDelHecho;

  @Column(name = "fecha_de_carga", nullable = false)
  private LocalDateTime fechaDeCarga;

  @Column(name = "fecha_ultima_modificacion")
  private LocalDateTime fechaUltimaModificacion;

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "multimedia_id", referencedColumnName = "id")
  private Multimedia contenido;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_origen")
  private TipoOrigen origen; // TODO: Definir tipo de dato: Origen o TipoOrigen

  @ManyToMany
  @JoinTable(
          name = "hecho_etiqueta",
          joinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "etiqueta_id", referencedColumnName = "id")
  )
  private List<Etiqueta> etiquetas;

  @Enumerated(EnumType.STRING)
  @ElementCollection
  @CollectionTable(name = "hechos_consensos", joinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  @Column(name = "consenso")
  private Set<TipoConsenso> consensos;

  @Column(name = "visibilidad")
  private Boolean visible;

  public static Hecho crear(
          Long identificadorExterno,
          String titulo,
          String descripcion,
          Categoria categoria,
          Ubicacion ubicacion,
          LocalDate fechaDelHecho,
          LocalDateTime fechaDeCarga,
          LocalDateTime fechaUltimaModificacion,
          Multimedia contenido,
          TipoOrigen origen,
          List<Etiqueta> etiquetas,
          Boolean visible
  ) {
    return Hecho
            .builder()
            .id(null)
            .idExterno(identificadorExterno)
            .fuente(null)
            .titulo(titulo)
            .descripcion(descripcion)
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(fechaDelHecho)
            .fechaDeCarga(fechaDeCarga)
            .fechaUltimaModificacion(fechaUltimaModificacion)
            .contenido(contenido)
            .origen(origen)
            .etiquetas(etiquetas)
            .consensos(new HashSet<>())
            .visible(visible)
            .build();
  }

  public void agregarEtiqueta(Etiqueta nuevaEtiqueta) {
    this.etiquetas.add(nuevaEtiqueta);
  }

  public void curar(TipoConsenso nuevoConsenso) {
    this.consensos.add(nuevoConsenso);
  }

  public void quitarCurado() {
    this.consensos.clear();
  }

  public boolean cumple(TipoConsenso consenso) {
    return this.consensos.contains(consenso);
  }

  public boolean tieneMismosAtributos(Hecho hecho) {
    return Objects.equals(this.getTitulo(), hecho.getTitulo()) &&
            Objects.equals(this.getCategoria(), hecho.getCategoria()) &&
            Objects.equals(this.getDescripcion(), hecho.getDescripcion()) &&
            Objects.equals(this.getUbicacion(), hecho.getUbicacion()) &&
            Objects.equals(this.getFechaDelHecho(), hecho.getFechaDelHecho());
  }

  public HechoOutputDTO toDTO() {
    return new HechoOutputDTO(
            this.id,
            this.idExterno,
            this.fuente != null ? this.fuente.toDTO() : null,
            this.titulo,
            this.descripcion,
            this.categoria != null ? this.categoria.toDTO() : null,
            this.ubicacion != null ? this.ubicacion.toDTO() : null,
            this.fechaDelHecho,
            this.fechaDeCarga,
            this.fechaUltimaModificacion,
            this.contenido != null ? this.contenido.toDTO() : null,
            this.origen,
            this.etiquetas != null ? this.etiquetas.stream().map(Etiqueta::toDTO).toList() : null,
            this.visible
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Hecho hecho = (Hecho) o;
    return id != null && Objects.equals(id, hecho.id);
  }

  @Override
  public int hashCode() {
    // Usar una constante para entidades sin id (nuevas)
    // o el id para entidades persistidas
    return id != null ? Objects.hash(id) : System.identityHashCode(this);
  }
}
