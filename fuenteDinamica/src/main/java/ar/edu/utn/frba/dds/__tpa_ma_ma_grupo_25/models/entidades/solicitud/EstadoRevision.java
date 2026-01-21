package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadoRevisionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "estado_revision")
public class EstadoRevision {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_estado")
  private TipoEstadoRevision tipoEstado;

  @Column(name = "fecha_resolucion")
  private LocalDateTime fechaResolucion;

  @ManyToOne
  @JoinColumn(name = "administrador_responsable_id", referencedColumnName = "id")
  private Administrador administradorResponsable;

  @Column(name = "descripcion")
  private String feedback;

  // Relaciones inversas hacia las solicitudes
  @ManyToOne
  @JoinColumn(name = "solicitud_creacion_id")
  private SolicitudDeCreacion solicitudDeCreacion;

  @ManyToOne
  @JoinColumn(name = "solicitud_modificacion_id")
  private SolicitudDeModificacion solicitudDeModificacion;

  // Constructores específicos
  public EstadoRevision(TipoEstadoRevision tipoEstado, String feedback, SolicitudDeCreacion solicitud) {
    this.tipoEstado = tipoEstado;
    this.feedback = feedback;
    this.solicitudDeCreacion = solicitud;
  }

  public EstadoRevision(TipoEstadoRevision tipoEstado, String feedback, SolicitudDeModificacion solicitud) {
    this.tipoEstado = tipoEstado;
    this.feedback = feedback;
    this.solicitudDeModificacion = solicitud;
  }

  public void procesar(Administrador administradorResponsable) {
    if (!fueProcesado()) {
      this.fechaResolucion = LocalDateTime.now();
      this.administradorResponsable = administradorResponsable;
    }
  }

  public boolean fueProcesado() {
    return this.administradorResponsable != null;
  }

  public EstadoRevisionOutputDTO toDTO() {
    return new EstadoRevisionOutputDTO(
            id,
            tipoEstado,
            fechaResolucion,
            administradorResponsable != null ? administradorResponsable.toDTO() : null,
            feedback
    );
  }
}
