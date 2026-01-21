package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudDTO {
  private Long identificador;
  private EstadoAtencionSolicitud estadoAtencionSolicitud;
  private HechoDTO hecho;
  private String justificacion;
  private ContribuyenteSimple contribuyente;
  private LocalDateTime fechaCreacion;
  private AdministradorDTO administradorResponsable;
  private LocalDateTime fechaResolucion;
}
