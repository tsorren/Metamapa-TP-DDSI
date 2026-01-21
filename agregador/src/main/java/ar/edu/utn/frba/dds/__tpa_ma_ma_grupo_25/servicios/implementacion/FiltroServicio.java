package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FiltroInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FiltroOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoContenido;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFiltroRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFiltroServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FiltroServicio implements IFiltroServicio {
  private final IFiltroRepositorioBD filtroRepositorio;
  private final NormalizadorCategoria normalizadorCategoria;

  public FiltroServicio(IFiltroRepositorioBD filtroRepositorio,
                        NormalizadorCategoria normalizadorCategoria) {
    this.filtroRepositorio = filtroRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Filtro> encontrarTodos() {
    return filtroRepositorio.findAll();
  }

  @Override
  @Transactional
  public Filtro crearDeCargaEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    Filtro filtro = new FiltroCargaEntre(fechaInicio, fechaFin);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Carga Entre con id {}", filtro.getId());
    return filtro;
  }

  // Crear filtro de categoría por nombre
  @Override
  @Transactional
  public Filtro crearDeCategoria(String nombreCategoria) {
    // Crear categoría con el nombre (será normalizada al persistir)
    var categoria = normalizadorCategoria.normalizar(new Categoria(nombreCategoria));
    Filtro filtro = new FiltroCategoria(categoria);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Categoria con nombre '{}' y id {}", nombreCategoria, filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeDescripcion(String descripcion) {
    Filtro filtro = new FiltroDescripcion(descripcion);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Descripcion con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeFechaEntre(LocalDate fechaInicio, LocalDate fechaFin) {
    Filtro filtro = new FiltroFechaEntre(fechaInicio, fechaFin);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Fecha Entre con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeFechaExacta(LocalDate fecha) {
    Filtro filtro = new FiltroFechaExacta(fecha);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Fecha Exacta con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeMultimedia(TipoContenido tipoContenido) {
    Filtro filtro = new FiltroMultimedia(tipoContenido);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Contenido Multimedia con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeTipoOrigen(TipoOrigen tipoOrigen) {
    Filtro filtro = new FiltroTipoOrigen(tipoOrigen);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Tipo Origen con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeTitulo(String titulo) {
    Filtro filtro = new FiltroTitulo(titulo);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Titulo con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public Filtro crearDeUbicacion(Double latitud, Double longitud, Double radio) {
    Filtro filtro = new FiltroUbicacion(new Ubicacion(latitud, longitud), radio);
    filtroRepositorio.save(filtro);
    log.info("<CREACION> Filtro de Ubicacion con id {}", filtro.getId());
    return filtro;
  }

  @Override
  @Transactional
  public FiltroOutputDTO crear(FiltroInputDTO dto) {
    Filtro filtro = switch (dto.getTipoFiltro()) {
      case "cargaEntre" -> this.crearDeCargaEntre(
              LocalDateTime.parse(dto.getParametros().get(0)),
              LocalDateTime.parse(dto.getParametros().get(1))
      );
      case "categoria" -> this.crearDeCategoria(dto.getParametros().get(0));
      case "descripcion" -> this.crearDeDescripcion(dto.getParametros().get(0));
      case "fechaEntre" -> this.crearDeFechaEntre(
              LocalDate.parse(dto.getParametros().get(0)),
              LocalDate.parse(dto.getParametros().get(1))
      );
      case "fechaExacta" -> this.crearDeFechaExacta(LocalDate.parse(dto.getParametros().get(0)));
      case "multimedia" -> this.crearDeMultimedia(TipoContenido.valueOf(dto.getParametros().get(0)));
      case "tipoOrigen" -> this.crearDeTipoOrigen(TipoOrigen.valueOf(dto.getParametros().get(0)));
      case "titulo" -> this.crearDeTitulo(dto.getParametros().get(0));
      case "ubicacion" -> this.crearDeUbicacion(
              Double.parseDouble(dto.getParametros().get(0)),
              Double.parseDouble(dto.getParametros().get(1)),
              Double.parseDouble(dto.getParametros().get(2))
      );
      default -> throw new IllegalArgumentException("Tipo de filtro no soportado: " + dto.getTipoFiltro());
    };
    return filtro.toDTO();
  }

}
