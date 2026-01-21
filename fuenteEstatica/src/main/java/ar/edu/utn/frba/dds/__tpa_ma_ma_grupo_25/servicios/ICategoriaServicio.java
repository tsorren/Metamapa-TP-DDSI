package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;
import java.util.List;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CategoriaInputDTO;



public interface ICategoriaServicio {
  List<CategoriaOutputDTO> findAll();

  CategoriaOutputDTO findByNombre(String nombre);

  CategoriaOutputDTO sincronizar(CategoriaInputDTO input);
}

