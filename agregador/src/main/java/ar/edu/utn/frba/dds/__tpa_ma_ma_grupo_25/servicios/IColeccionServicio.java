package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ColeccionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ConsultaColeccionOutputDTO;

import java.util.List;

public interface IColeccionServicio {
  ConsultaColeccionOutputDTO consultarColeccion(NavegacionInputDTO inputDTO);

  ColeccionOutputDTO crear(ColeccionInputDTO coleccionInputDto);

  List<ColeccionOutputDTO> encontrarTodas();

  void eliminar(String handle);

  ColeccionOutputDTO modificar(String handle, ColeccionInputDTO coleccionInput);

  ColeccionOutputDTO agregarFuente(String handle, Long idFuente);

  ColeccionOutputDTO quitarFuente(String handle, Long idFuente);
}
