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
@Table(name = "Hecho")
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

  @Embedded //hace que tome los atributos del objeto y los sume como columna a la tabla de hechos sin crear otra tabla
  @AttributeOverrides({
          @AttributeOverride(name = "latitud", column = @Column(name = "latitud")),
          @AttributeOverride(name = "longitud", column = @Column(name = "longitud"))
  })
  private Ubicacion ubicacion;

  @Column(name = "fechaDelHecho")
  private LocalDate fechaDelHecho;
  @Column(name = "fechaDeCarga")
  private LocalDateTime fechaDeCarga;

  @OneToOne
  @JoinColumn(name = "multimedia_id", referencedColumnName = "id")
  private Multimedia contenido;

  @Enumerated(EnumType.STRING)
  @Column(name = "origen")
  private TipoOrigen origen; // TODO: Definir tipo de dato: Origen o TipoOrigen

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "etiqueta_id", referencedColumnName = "id")
  private List<Etiqueta> etiquetas;

  @Column(name = "visible")
  private Boolean visible;
  @Column(name = "fechaUltimaModificacion")
  private LocalDateTime fechaUltimaModificacion;

  @ManyToOne
  @JoinColumn(name = "fuente_id")
  private FuenteEstatica fuente;


  public static Hecho crear(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fecha, TipoOrigen origen) {
    return Hecho
            .builder()
            .id(null)
            .titulo(titulo)
            .descripcion(descripcion)
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(fecha)
            .fechaDeCarga(LocalDateTime.now())
            .fechaUltimaModificacion(LocalDateTime.now())
            .origen(origen)
            .contenido(null)
            .etiquetas(new ArrayList<>())
            .visible(true)
            .build();
  }

  public void ocultar() {
    this.setVisible(false);
  }

  public void agregarEtiqueta(Etiqueta nuevaEtiqueta) {
    this.etiquetas.add(nuevaEtiqueta);
  }

  public HechoOutputDTO toDTO() {
    return new HechoOutputDTO(
            this.id,
            this.titulo,
            this.descripcion,
            this.categoria,
            this.ubicacion,
            this.fechaDelHecho,
            this.fechaDeCarga,
            this.fechaUltimaModificacion,
            this.contenido,
            this.origen,
            this.etiquetas,
            this.visible
    );
  }

}
