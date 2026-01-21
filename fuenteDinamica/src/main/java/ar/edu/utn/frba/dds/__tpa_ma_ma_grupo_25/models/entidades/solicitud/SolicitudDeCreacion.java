package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solicitud_de_creacion")
public class SolicitudDeCreacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

  @OneToMany(mappedBy = "solicitudDeCreacion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EstadoRevision> estados = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  private Hecho hecho;

  @ManyToOne
  @JoinColumn(name = "contribuyente_id", referencedColumnName = "id")
  private Contribuyente contribuyente;

  public SolicitudDeCreacion(Hecho hecho, Contribuyente contribuyente) {
    this.hecho = hecho;
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

  public SolicitudDeCreacionOutputDTO toDTO() {
    return new SolicitudDeCreacionOutputDTO(
            id,
            fechaCreacion,
            estados.stream().map(EstadoRevision::toDTO).toList(),
            hecho.toDTO(),
            contribuyente != null ? contribuyente.toDTO() : null
    );
  }
}
