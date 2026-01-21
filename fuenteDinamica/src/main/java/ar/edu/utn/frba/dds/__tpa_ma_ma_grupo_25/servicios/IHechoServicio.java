package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;

import java.time.LocalDate;
import java.util.List;

public interface IHechoServicio {
  List<HechoOutputDTO> encontrarTodos();

  HechoOutputDTO encontrarPorId(Long id);

  Hecho crear(String titulo, String descripcion, Categoria categoria, Double latitud, Double longitud, LocalDate fechaDelHecho, Long idContenido, Boolean visible);

  HechoOutputDTO modificarHecho(HechoModificacionDTO modificador);

  void save(Hecho hecho);

  void ocultarHecho(Long idHecho);

  void modificarHecho(Hecho hecho);
}
