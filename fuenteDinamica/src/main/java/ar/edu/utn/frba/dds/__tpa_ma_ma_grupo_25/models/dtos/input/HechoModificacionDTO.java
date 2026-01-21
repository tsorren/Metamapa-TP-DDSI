package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HechoModificacionDTO {
  private final Long idHecho;
  private final Long idContribuyente;
  private final String titulo;
  private final String descripcion;
  private final Categoria categoria;
  private final Ubicacion ubicacion;
  private final LocalDate fechaDelHecho;
  private final Multimedia contenido;
}
