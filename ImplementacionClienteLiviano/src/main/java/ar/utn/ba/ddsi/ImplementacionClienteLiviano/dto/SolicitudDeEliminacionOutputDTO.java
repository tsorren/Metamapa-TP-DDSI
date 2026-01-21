package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDeEliminacionOutputDTO {
  private Long id;
  private LocalDateTime fechaCreacion;
  private EstadoAtencionSolicitud estadoAtencionSolicitud;
  private HechoDTO hecho;
  private String justificacion;
  private ContribuyenteOutputDTO contribuyente;
  private AdministradorOutputDTO administradorResponsable;
  private LocalDateTime fechaResolucion;
  private String justificacionRechazo;
}
