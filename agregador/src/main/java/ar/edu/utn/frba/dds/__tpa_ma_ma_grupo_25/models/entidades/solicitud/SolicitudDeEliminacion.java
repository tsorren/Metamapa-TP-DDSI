package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeEliminacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "solicitudes_de_eliminacion")
public class SolicitudDeEliminacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;


  @Enumerated(EnumType.STRING)
  @Column(name = "estado_atencion")
  private EstadoAtencionSolicitud estadoAtencionSolicitud;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  private Hecho hecho;

  @Column(name = "justificacion", columnDefinition = "VARCHAR(2000)")
  private String justificacion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contribuyente", referencedColumnName = "id")
  private Contribuyente contribuyente;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "administrador_responsable", referencedColumnName = "id")
  private Administrador administradorResponsable;

  @Column(name = "fecha_resolucion")
  private LocalDateTime fechaResolucion;

  @Column(name = "justificacion_rechazo")
  private String justificacionRechazo;


  public SolicitudDeEliminacion(Hecho hecho, String justificacion, Contribuyente contribuyente) {
    this.estadoAtencionSolicitud = EstadoAtencionSolicitud.PENDIENTE;
    this.hecho = hecho;
    if (justificacion.length() > 2000) {
      throw new IllegalArgumentException("La justificación no puede exceder los 2000 caracteres.");
    }
    this.justificacion = justificacion;
    this.contribuyente = contribuyente;
    this.fechaCreacion = LocalDateTime.now();

  }

  public void marcarAceptada(Administrador responsable) {
    if (this.estadoAtencionSolicitud != EstadoAtencionSolicitud.PENDIENTE) {
      return;
    }
    this.estadoAtencionSolicitud = EstadoAtencionSolicitud.ACEPTADA;
    this.administradorResponsable = responsable;
    this.fechaResolucion = LocalDateTime.now();
    hecho.setVisible(false);
  }

  public void marcarRechazada(Administrador responsable, String justificacion) {
    if (this.estadoAtencionSolicitud != EstadoAtencionSolicitud.PENDIENTE) {
      return;
    }
    this.estadoAtencionSolicitud = EstadoAtencionSolicitud.RECHAZADA;
    this.administradorResponsable = responsable;
    this.justificacionRechazo = justificacion;
    this.fechaResolucion = LocalDateTime.now();
  }

  public SolicitudDeEliminacionOutputDTO toDTO() {
    return new SolicitudDeEliminacionOutputDTO(
            this.id,
            this.fechaCreacion,
            this.estadoAtencionSolicitud,
            this.hecho.toDTO(),
            this.justificacion,
            this.contribuyente != null ? this.contribuyente.toDTO() : null,
            this.administradorResponsable != null ? this.administradorResponsable.toDTO() : null,
            this.fechaResolucion,
            this.justificacionRechazo
    );
  }
}
