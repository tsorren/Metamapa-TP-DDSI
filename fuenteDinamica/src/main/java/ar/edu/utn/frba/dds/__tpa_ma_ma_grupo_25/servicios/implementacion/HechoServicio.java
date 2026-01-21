package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContenidoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HechoServicio implements IHechoServicio {

  @Getter
  private final IHechoRepositorioBD hechoRepositorio;
  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final IContenidoRepositorioBD contenidoRepositorio;
  private final NormalizadorCategoria normalizadorCategoria;

  public HechoServicio(
          IHechoRepositorioBD hechoRepositorio,
          ICategoriaRepositorioBD categoriaRepositorio,
          IContenidoRepositorioBD contenidoRepositorio,
          NormalizadorCategoria normalizadorCategoria
  ) {
    this.hechoRepositorio = hechoRepositorio;
    this.categoriaRepositorio = categoriaRepositorio;
    this.contenidoRepositorio = contenidoRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
  }


  // -------------------- Métodos de interfaz --------------------
  @Override
  @Transactional(readOnly = true)
  public List<HechoOutputDTO> encontrarTodos() {
    return hechoRepositorio.findAll()
            .stream()
            .map(Hecho::toDTO)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public HechoOutputDTO encontrarPorId(Long id) {
    return hechoRepositorio.findById(id)
            .map(Hecho::toDTO)
            .orElseThrow();
  }

  @Override
  @Transactional
  public Hecho crear(String titulo, String descripcion, Categoria categoria,
                     Double latitud, Double longitud, LocalDate fechaDelHecho,
                     Long idContenido, Boolean visible) {

    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(categoria);
    Ubicacion ubicacion = new Ubicacion(latitud, longitud);
    Multimedia contenido = null;
    if (idContenido != null) {
      log.debug("Buscando multimedia con id={}", idContenido);
      contenido = contenidoRepositorio.findById(idContenido)
              .orElseThrow(() -> {
                log.error("Multimedia no encontrada con id={}", idContenido);
                return new RuntimeException("Multimedia no encontrada con id: " + idContenido);
              });
    }
    return Hecho.crear(
            titulo,
            descripcion,
            categoriaNormalizada,
            ubicacion,
            fechaDelHecho,
            TipoOrigen.CONTRIBUYENTE,
            contenido
    );
  }

  @Override
  @Transactional
  public void save(Hecho hecho) {
    hecho.setFechaUltimaModificacion(LocalDateTime.now());

    // Re-cargar la multimedia para asegurar que esté en el contexto de persistencia actual
    if (hecho.getContenido() != null && hecho.getContenido().getId() != null) {
      Multimedia multimedia = contenidoRepositorio.findById(hecho.getContenido().getId())
              .orElseThrow(() -> new RuntimeException("Multimedia no encontrada con id: " + hecho.getContenido().getId()));
      hecho.setContenido(multimedia);
    }

    hechoRepositorio.save(hecho);
  }

  @Override
  @Transactional
  public HechoOutputDTO modificarHecho(HechoModificacionDTO dto) {
    Hecho h = hechoRepositorio.findById(dto.getIdHecho()).orElseThrow();
    actualizarHecho(h, dto);
    hechoRepositorio.save(h);
    return h.toDTO();
  }

  @Override
  @Transactional
  public void ocultarHecho(Long idHecho) {
    Hecho h = hechoRepositorio.findById(idHecho).orElseThrow();
    h.setVisible(false);
    hechoRepositorio.save(h);
  }


  @Override
  @Transactional
  public void modificarHecho(Hecho modificador) {
    Hecho h = hechoRepositorio.findById(modificador.getId()).orElseThrow();

    if (modificador.getTitulo() != null) h.setTitulo(modificador.getTitulo());
    if (modificador.getDescripcion() != null) h.setDescripcion(modificador.getDescripcion());
    if (modificador.getCategoria() != null)
      h.setCategoria(normalizadorCategoria.normalizar(modificador.getCategoria()));
    if (modificador.getUbicacion() != null) h.setUbicacion(modificador.getUbicacion());
    if (modificador.getFechaDelHecho() != null) h.setFechaDelHecho(modificador.getFechaDelHecho());

    // Re-cargar la multimedia desde el contexto de persistencia si se modifica
    if (modificador.getContenido() != null && modificador.getContenido().getId() != null) {
      Multimedia multimediaPersistida = contenidoRepositorio.findById(modificador.getContenido().getId())
              .orElseThrow(() -> new RuntimeException("Multimedia no encontrada con id: " + modificador.getContenido().getId()));
      h.setContenido(multimediaPersistida);
    } else if (modificador.getContenido() != null) {
      h.setContenido(modificador.getContenido());
    }

    h.setFechaUltimaModificacion(LocalDateTime.now());
    hechoRepositorio.save(h);
  }

  // -------------------- Métodos privados auxiliares --------------------
  private void actualizarHecho(Hecho h, HechoModificacionDTO dto) {
    if (dto.getTitulo() != null) h.setTitulo(dto.getTitulo());
    if (dto.getDescripcion() != null) h.setDescripcion(dto.getDescripcion());
    if (dto.getCategoria() != null) h.setCategoria(normalizadorCategoria.normalizar(dto.getCategoria()));
    if (dto.getUbicacion() != null) h.setUbicacion(dto.getUbicacion());
    if (dto.getFechaDelHecho() != null) h.setFechaDelHecho(dto.getFechaDelHecho());

    // Re-cargar la multimedia desde el contexto de persistencia si se actualiza
    if (dto.getContenido() != null && dto.getContenido().getId() != null) {
      Multimedia multimediaPersistida = contenidoRepositorio.findById(dto.getContenido().getId())
              .orElseThrow(() -> new RuntimeException("Multimedia no encontrada con id: " + dto.getContenido().getId()));
      h.setContenido(multimediaPersistida);
    } else if (dto.getContenido() != null) {
      h.setContenido(dto.getContenido());
    }

    h.setFechaUltimaModificacion(LocalDateTime.now());
  }
}
