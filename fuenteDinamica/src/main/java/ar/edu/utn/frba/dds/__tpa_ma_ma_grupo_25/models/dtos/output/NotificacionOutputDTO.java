package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificacionOutputDTO {
  private Long id;
  private String mensaje;
  private LocalDateTime fechaCreacion;
  private Boolean leida;
  private TipoNotificacion tipoNotificacion;
  private AdministradorOutputDTO administrador;
  private ContribuyenteOutputDTO contribuyente;
  private Long solicitudId;
  private TipoSolicitud tipoSolicitud;
}
