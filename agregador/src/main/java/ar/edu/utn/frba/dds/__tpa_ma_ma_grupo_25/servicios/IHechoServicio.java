package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;

import java.time.LocalDate;
import java.util.List;

public interface IHechoServicio {
  List<HechoOutputDTO> encontrarTodos(HechosFiltrandoInputDTO i);

  HechoOutputDTO encontrarPorId(Long id);

  HechoOutputDTO etiquetar(Long idHecho, Long idEtiqueta);

  void guardar(Hecho h);

  void guardarTodos(List<Hecho> hechos);

  List<Hecho> hechosDeFuente(Fuente f);

  Hecho crear(String titulo, String descripcion, Categoria categoria, Double latitud, Double longitud, LocalDate fechaDelHecho, Long idContenido, Boolean visible);

  Hecho hechoInputDTO(HechoInputDTO h);

  HechoOutputDTO findByFuenteIdAndIdExterno(Long idFuente, Long idExterno);
}
