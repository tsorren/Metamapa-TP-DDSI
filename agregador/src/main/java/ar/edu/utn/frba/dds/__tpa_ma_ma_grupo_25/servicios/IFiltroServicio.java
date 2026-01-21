package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FiltroInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FiltroOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.Filtro;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoContenido;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IFiltroServicio {

  List<Filtro> encontrarTodos();

  Filtro crearDeCargaEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin);

  Filtro crearDeCategoria(String nombreCategoria);

  Filtro crearDeDescripcion(String descripcion);

  Filtro crearDeFechaEntre(LocalDate fechaInicio, LocalDate fechaFin);

  Filtro crearDeFechaExacta(LocalDate fecha);

  Filtro crearDeMultimedia(TipoContenido tipoContenido);

  Filtro crearDeTipoOrigen(TipoOrigen tipoOrigen);

  Filtro crearDeTitulo(String titulo);

  Filtro crearDeUbicacion(Double latitud, Double longitud, Double radio);

  FiltroOutputDTO crear(FiltroInputDTO dto);

}
