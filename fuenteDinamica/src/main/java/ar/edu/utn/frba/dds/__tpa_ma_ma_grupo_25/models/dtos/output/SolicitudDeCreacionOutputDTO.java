package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SolicitudDeCreacionOutputDTO {
  private final Long identificador;
  private final LocalDateTime fechaCreacion;
  private final List<EstadoRevisionOutputDTO> estadosDTO;
  private final HechoOutputDTO hecho;
  private final ContribuyenteOutputDTO contribuyente;
}
