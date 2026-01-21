package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.NotificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notificacion")
public class Notificacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String mensaje;

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Column(nullable = false)
  private Boolean leida = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_notificacion", nullable = false)
  private TipoNotificacion tipoNotificacion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "administrador_id")
  private Administrador administrador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contribuyente_id")
  private Contribuyente contribuyente;

  @Column(name = "solicitud_id")
  private Long solicitudId;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_solicitud")
  private TipoSolicitud tipoSolicitud;

  public Notificacion(String mensaje, TipoNotificacion tipoNotificacion, Administrador administrador, Long solicitudId, TipoSolicitud tipoSolicitud) {
    this.mensaje = mensaje;
    this.tipoNotificacion = tipoNotificacion;
    this.administrador = administrador;
    this.solicitudId = solicitudId;
    this.tipoSolicitud = tipoSolicitud;
    this.fechaCreacion = LocalDateTime.now();
    this.leida = false;
  }

  public Notificacion(String mensaje, TipoNotificacion tipoNotificacion, Contribuyente contribuyente, Long solicitudId, TipoSolicitud tipoSolicitud) {
    this.mensaje = mensaje;
    this.tipoNotificacion = tipoNotificacion;
    this.contribuyente = contribuyente;
    this.solicitudId = solicitudId;
    this.tipoSolicitud = tipoSolicitud;
    this.fechaCreacion = LocalDateTime.now();
    this.leida = false;
  }

  public void marcarComoLeida() {
    this.leida = true;
  }

  @PrePersist
  public void prePersist() {
    if (fechaCreacion == null) {
      fechaCreacion = LocalDateTime.now();
    }
    if (leida == null) {
      leida = false;
    }
  }

  public NotificacionOutputDTO toDTO() {
    return new NotificacionOutputDTO(
            id,
            mensaje,
            fechaCreacion,
            leida,
            tipoNotificacion,
            administrador != null ? administrador.toDTO() : null,
            contribuyente != null ? contribuyente.toDTO() : null,
            solicitudId,
            tipoSolicitud
    );
  }
}
