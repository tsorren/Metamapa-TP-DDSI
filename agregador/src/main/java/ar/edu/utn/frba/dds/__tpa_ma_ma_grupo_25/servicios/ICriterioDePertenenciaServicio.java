package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CriterioDePertenenciaOutputDTO;

import java.util.List;

public interface ICriterioDePertenenciaServicio {
  List<CriterioDePertenenciaOutputDTO> encontrarTodos();

  CriterioDePertenenciaOutputDTO encontrarPorId(Long id);

  CriterioDePertenenciaOutputDTO crear();

  CriterioDePertenenciaOutputDTO agregarFiltro(Long idCriterio, Long idFiltro);

  CriterioDePertenenciaOutputDTO quitarFiltro(Long idCriterio, Long idFiltro);
}
