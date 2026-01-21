package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import java.time.LocalDateTime;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificacionDTO {
  private String tipoSolicitud;
  private Long solicitudId;
  private ContribuyenteDTO contribuyente;
  private AdministradorDTO administrador;
  private String tipoNotificacion;
  private Boolean leida;
  private LocalDateTime fechaCreacion;
  private String mensaje;
  private Long id;
}




