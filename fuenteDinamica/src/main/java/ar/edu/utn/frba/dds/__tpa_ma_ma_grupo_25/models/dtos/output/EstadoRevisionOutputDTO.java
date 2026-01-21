package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EstadoRevisionOutputDTO {
  private final Long identificador;
  private final TipoEstadoRevision tipoEstado;
  private final LocalDateTime fechaResolucion;
  private final AdministradorOutputDTO administradorResponsable;
  private final String feedback;
}
