package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCreacionOutputDTO {
  private Long identificador;
  private LocalDateTime fechaCreacion;
  private List<EstadoRevisionOutputDTO> estadosDTO;
  private HechoDTO hecho;
  private ContribuyenteOutputDTO contribuyente;
}
