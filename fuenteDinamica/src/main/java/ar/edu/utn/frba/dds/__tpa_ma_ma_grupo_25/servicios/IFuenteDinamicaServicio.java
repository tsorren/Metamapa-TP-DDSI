package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteDinamicaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;

import java.util.List;

public interface IFuenteDinamicaServicio {
  FuenteDinamicaOutputDTO crear();

  List<FuenteDinamicaOutputDTO> encontrarTodas();

  List<HechoOutputDTO> hechosDeFuente(Long identificador);

  HechoOutputDTO cargarHecho(HechoInputDTO input);

  HechoOutputDTO modificarHecho(HechoModificacionDTO modificacion);

  HechoOutputDTO hechoPorId(Long idFuente, Long idHecho);

  EstadisticasFuenteDinamicaDTO obtenerEstadisticas();
}
