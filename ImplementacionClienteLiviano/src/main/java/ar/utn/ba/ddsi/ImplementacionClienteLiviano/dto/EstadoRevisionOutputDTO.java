package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRevisionOutputDTO {
  private Long identificador;
  private TipoEstadoRevision tipoEstado;
  private LocalDateTime fechaResolucion;
  private AdministradorOutputDTO administradorResponsable;
  private String feedback;
}
