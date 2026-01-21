package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SolicitudDeModificacionOutputDTO {
  private final Long identificador;
  private final LocalDateTime fechaCreacion;
  private final List<EstadoRevisionOutputDTO> estados;
  private final HechoOutputDTO hecho;
  private final HechoModificacionDTO modificacion;
  private final ContribuyenteOutputDTO contribuyente;
}
