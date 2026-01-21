package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hechos")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, name = "idExterno", columnDefinition = "LONG")
  private Long idExterno; // Identificador que provee la fuente

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fuente_id", referencedColumnName = "id", nullable = false)
  private Fuente fuente;

  @Column(name = "titulo", columnDefinition = "VARCHAR(50)")
  private String titulo;

  @Column(name = "descripcion")
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

  @OneToOne(fetch = FetchType.LAZY)
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
  private List<Etiqueta> etiquetas = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @ElementCollection
  @CollectionTable(name = "hechos_consensos", joinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id"))
  @Column(name = "consenso")
  private Set<TipoConsenso> consensos;

  // ✅ Bidireccional con FuenteDinamica
  @ManyToMany(mappedBy = "hechosAsociados")
  private List<Fuente> fuentes = new ArrayList<>();

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

}
