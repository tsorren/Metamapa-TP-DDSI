package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EtiquetaOutputDTO;

import java.util.List;

public interface IEtiquetaServicio {
  EtiquetaOutputDTO crear(String nombre);

  List<EtiquetaOutputDTO> encontrarTodas();

  EtiquetaOutputDTO encontrarPorId(Long idEtiqueta);

}
