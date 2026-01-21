package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContenidoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IEtiquetaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HechoServicio implements IHechoServicio {

  @Getter
  private final IHechoRepositorioBD hechoRepositorio;
  private final IEtiquetaRepositorioBD etiquetaRepositorio;
  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final IContenidoRepositorioBD contenidoRepositorio;

  private final NormalizadorCategoria normalizadorCategoria;

  public HechoServicio(IHechoRepositorioBD hechoRepositorio,
                       IEtiquetaRepositorioBD etiquetaRepositorio,
                       ICategoriaRepositorioBD categoriaRepo,
                       IContenidoRepositorioBD contenidoRepositorio,
                       NormalizadorCategoria normalizadorCategoria) {
    this.hechoRepositorio = hechoRepositorio;
    this.etiquetaRepositorio = etiquetaRepositorio;
    this.categoriaRepositorio = categoriaRepo;
    this.contenidoRepositorio = contenidoRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
  }


  // -----------------------------
  // Creación a partir de DTO de entrada
  // -----------------------------
  @Override
  public Hecho hechoInputDTO(HechoInputDTO h) {
    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(h.getCategoria());
    return Hecho.crear(
            h.getIdentificador(),
            h.getTitulo(),
            h.getDescripcion(),
            categoriaNormalizada,
            h.getUbicacion(),
            h.getFechaDelHecho(),
            h.getFechaDeCarga(),
            h.getFechaUltimaModificacion(),
            h.getContenido(),
            h.getOrigen(),
            h.getEtiquetas(),
            h.getVisible()
    );
  }

  @Override
  public HechoOutputDTO findByFuenteIdAndIdExterno(Long idFuente, Long idExterno) {
    return hechoRepositorio.findHechoByFuenteIdAndIdExterno(idFuente, idExterno).toDTO();
  }


  // -----------------------------
  // Filtrado y consultas
  // -----------------------------
  @Override
  @Transactional(readOnly = true)
  public List<HechoOutputDTO> encontrarTodos(HechosFiltrandoInputDTO i) {
    CriterioDePertenencia c = new CriterioDePertenencia();
    c.generarFiltros(i, false, null);
    List<Hecho> hechos = c.aplicar(hechoRepositorio.findAll());
    log.info("<CONSULTA> {} Hechos encontrados", hechos.size());
    return hechos.stream()
            .map(Hecho::toDTO)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public HechoOutputDTO encontrarPorId(Long id) {
    return hechoRepositorio.findById(id).orElseThrow().toDTO();
  }

  // -----------------------------
  // Etiquetado
  // -----------------------------
  @Override
  @Transactional
  public HechoOutputDTO etiquetar(Long idHecho, Long idEtiqueta) {
    Hecho hecho = hechoRepositorio.findById(idHecho).orElseThrow();
    hecho.agregarEtiqueta(etiquetaRepositorio.findById(idEtiqueta).orElse(null));
    return hecho.toDTO();
  }

  // -----------------------------
  // Persistencia
  // -----------------------------
  @Override
  @Transactional
  public void guardar(Hecho h) {
    // Normalizar el Multimedia para evitar entidades detached
    Multimedia contenidoNormalizado = normalizarMultimedia(h.getContenido());
    h.setContenido(contenidoNormalizado);

    // Verificar si ya existe un hecho con el mismo idExterno y fuente
    if (h.getFuente() != null && h.getIdExterno() != null) {
      Hecho existente = hechoRepositorio.findHechoByFuenteIdAndIdExterno(
          h.getFuente().getId(),
          h.getIdExterno()
      );

      if (existente != null) {
        // Actualizar el hecho existente con los nuevos datos
        log.debug("<UPDATE> Actualizando hecho existente id={} (idExterno={}) de fuente {}",
            existente.getId(), h.getIdExterno(), h.getFuente().getId());

        existente.setTitulo(h.getTitulo());
        existente.setDescripcion(h.getDescripcion());
        existente.setCategoria(h.getCategoria());
        existente.setUbicacion(h.getUbicacion());
        existente.setFechaDelHecho(h.getFechaDelHecho());
        existente.setFechaUltimaModificacion(h.getFechaUltimaModificacion());
        existente.setContenido(contenidoNormalizado);
        existente.setOrigen(h.getOrigen());
        existente.setVisible(h.getVisible());
        // No actualizamos fechaDeCarga porque es la fecha original de carga

        hechoRepositorio.save(existente);
        return;
      }
    }

    // Si no existe, crear nuevo
    /*
    log.info("<INSERT> Creando nuevo hecho idExterno={} para fuente {}",
        h.getIdExterno(), h.getFuente() != null ? h.getFuente().getId() : "null");

     */
    hechoRepositorio.save(h);
  }

  @Override
  @Transactional
  public void guardarTodos(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) {
      return;
    }

    // Procesar cada hecho de forma síncrona
    List<Hecho> hechosAGuardar = hechos.stream()
        .map(this::procesarHechoParaGuardar)
        .filter(java.util.Objects::nonNull)
        .toList();

    // Guardar todos en batch
    if (!hechosAGuardar.isEmpty()) {
      hechoRepositorio.saveAll(hechosAGuardar);
      log.debug("<BATCH> Guardados {} hechos en batch", hechosAGuardar.size());
    }
  }

  /**
   * Procesa un hecho para prepararlo para guardado: normaliza multimedia y verifica existencia.
   * @return El hecho a guardar (nuevo o existente actualizado)
   */
  private Hecho procesarHechoParaGuardar(Hecho h) {
    try {
      // Normalizar el Multimedia
      Multimedia contenidoNormalizado = normalizarMultimedia(h.getContenido());
      h.setContenido(contenidoNormalizado);

      // Verificar si ya existe
      if (h.getFuente() != null && h.getIdExterno() != null) {
        Hecho existente = hechoRepositorio.findHechoByFuenteIdAndIdExterno(
            h.getFuente().getId(),
            h.getIdExterno()
        );

        if (existente != null) {
          // Actualizar existente
          existente.setTitulo(h.getTitulo());
          existente.setDescripcion(h.getDescripcion());
          existente.setCategoria(h.getCategoria());
          existente.setUbicacion(h.getUbicacion());
          existente.setFechaDelHecho(h.getFechaDelHecho());
          existente.setFechaUltimaModificacion(h.getFechaUltimaModificacion());
          existente.setContenido(contenidoNormalizado);
          existente.setOrigen(h.getOrigen());
          existente.setVisible(h.getVisible());
          return existente;
        }
      }

      return h;
    } catch (Exception e) {
      log.error("Error al procesar hecho para guardar: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Normaliza un objeto Multimedia para evitar problemas de entidad detached.
   * Si el Multimedia tiene una URL que ya existe en la BD, retorna el existente.
   * Si no existe, crea uno nuevo (sin ID) para que se persista correctamente.
   */
  private Multimedia normalizarMultimedia(Multimedia multimedia) {
    if (multimedia == null) {
      return null;
    }

    // Buscar si ya existe un Multimedia con la misma URL
    if (multimedia.getUrl() != null) {
      Optional<Multimedia> existente = contenidoRepositorio.findByUrl(multimedia.getUrl());
      if (existente.isPresent()) {
        // Retornar el Multimedia existente que está gestionado por Hibernate
        return existente.get();
      }
    }

    // Si no existe, crear uno nuevo sin ID para que se persista como nuevo
    Multimedia nuevoMultimedia = new Multimedia();
    nuevoMultimedia.setTipoContenido(multimedia.getTipoContenido());
    nuevoMultimedia.setUrl(multimedia.getUrl());
    return nuevoMultimedia;
  }

  // -----------------------------
  // Hechos por fuente
  // -----------------------------
  @Override
  @Transactional(readOnly = true)
  public List<Hecho> hechosDeFuente(Fuente f) {
    // Usar findByFuenteWithFetch que carga la relación Fuente con JOIN FETCH
    // Esto evita LazyInitializationException cuando se llama a toDTO()
    return hechoRepositorio.findByFuenteWithFetch(f);
  }

  // -----------------------------
  // Creación directa (para contribuyentes)
  // -----------------------------
  @Override
  @Transactional
  public Hecho crear(String titulo,
                     String descripcion,
                     Categoria categoria,
                     Double latitud,
                     Double longitud,
                     LocalDate fechaDelHecho,
                     Long idContenido,
                     Boolean visible) {

    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(categoria);
    Ubicacion ubicacion = new Ubicacion(latitud, longitud);
    Multimedia contenido = contenidoRepositorio.findById(idContenido).orElseThrow();

    return Hecho.crear(
            null,
            titulo,
            descripcion,
            categoriaNormalizada,
            ubicacion,
            fechaDelHecho,
            LocalDateTime.now(),
            LocalDateTime.now(),
            contenido,
            TipoOrigen.CONTRIBUYENTE,
            new ArrayList<>(),
            visible
    );
  }
}
