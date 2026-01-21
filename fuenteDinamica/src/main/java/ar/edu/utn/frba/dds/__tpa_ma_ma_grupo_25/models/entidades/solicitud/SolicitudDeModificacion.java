package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeModificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solicitud_de_modificacion")
public class SolicitudDeModificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  @OneToMany(mappedBy = "solicitudDeModificacion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EstadoRevision> estados = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  private Hecho hecho;

  // Campos de la modificación propuesta (persistidos directamente)
  @Column(name = "modificacion_titulo")
  private String modificacionTitulo;

  @Column(name = "modificacion_descripcion")
  private String modificacionDescripcion;

  @ManyToOne
  @JoinColumn(name = "modificacion_categoria_id")
  private Categoria modificacionCategoria;

  @Embedded
  private Ubicacion modificacionUbicacion;

  @Column(name = "modificacion_fecha_del_hecho")
  private LocalDate modificacionFechaDelHecho;

  @ManyToOne
  @JoinColumn(name = "modificacion_contenido_id")
  private Multimedia modificacionContenido;

  @ManyToOne
  @JoinColumn(name = "contribuyente_id", referencedColumnName = "id")
  private Contribuyente contribuyente;

  public SolicitudDeModificacion(Hecho hecho, HechoModificacionDTO modificacion, Contribuyente contribuyente) {
    this.hecho = hecho;
    this.modificacionTitulo = modificacion.getTitulo();
    this.modificacionDescripcion = modificacion.getDescripcion();
    this.modificacionCategoria = modificacion.getCategoria();
    this.modificacionUbicacion = modificacion.getUbicacion();
    this.modificacionFechaDelHecho = modificacion.getFechaDelHecho();
    this.modificacionContenido = modificacion.getContenido();
    this.contribuyente = contribuyente;
    this.fechaCreacion = LocalDateTime.now();
    this.estados.add(new EstadoRevision(TipoEstadoRevision.PENDIENTE, "En espera de procesamiento", this));
  }

  public static boolean esEstadoFinal(TipoEstadoRevision tipoEstado) {
    return tipoEstado == TipoEstadoRevision.RECHAZADA || tipoEstado == TipoEstadoRevision.ACEPTADA;
  }

  @PrePersist
  public void prePersist() {
    if (fechaCreacion == null)
      fechaCreacion = LocalDateTime.now();
  }

  public void procesar(Administrador administrador, TipoEstadoRevision nuevoEstado, String feedback) {
    if (!cerrada()) {
      estadoActual().procesar(administrador);
      estados.add(new EstadoRevision(nuevoEstado, feedback, this));
      if (esEstadoFinal(nuevoEstado)) {
        estadoActual().procesar(administrador);
      }
    }
  }

  public EstadoRevision estadoActual() {
    return estados.get(estados.size() - 1);
  }

  public boolean cerrada() {
    return esEstadoFinal(estadoActual().getTipoEstado());
  }

  public LocalDateTime ultimaFechaProcesamiento() {
    EstadoRevision anterior = estadoAnterior();
    return anterior == null ? null : anterior.getFechaResolucion();
  }

  public EstadoRevision estadoAnterior() {
    return estados.size() > 1 ? estados.get(estados.size() - 2) : null;
  }

  // Reconstruye el DTO de modificación a partir de los campos persistidos
  public HechoModificacionDTO getModificacion() {
    return new HechoModificacionDTO(
            hecho.getId(),
            contribuyente.getId(),
            modificacionTitulo,
            modificacionDescripcion,
            modificacionCategoria,
            modificacionUbicacion,
            modificacionFechaDelHecho,
            modificacionContenido
    );
  }
  public SolicitudDeModificacionOutputDTO toDTO() {
    return new SolicitudDeModificacionOutputDTO(
            id,
            fechaCreacion,
            estados.stream().map(EstadoRevision::toDTO).toList(),
            hecho.toDTO(),
            this.getModificacion(),
            contribuyente != null ? contribuyente.toDTO() : null
    );
  }


}
