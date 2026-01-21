package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;

import java.time.LocalDateTime;
import java.util.List;

public interface IFuenteServicio {
  FuenteOutputDTO crear(ModuloFuente modulo, FuenteInputDTO i);

  List<Fuente> encontrarTodas();

  List<FuenteOutputDTO> encontrarTodasDTO();

  Fuente encontrarPorId(Long identificador);

  List<Hecho> hechosDeFuente(Long identificador);

  void refrescarHechosDeFuentes(LocalDateTime ultimoRefresco);

  HechoOutputDTO hechoDeFuenteDinamica(Long id);

}
