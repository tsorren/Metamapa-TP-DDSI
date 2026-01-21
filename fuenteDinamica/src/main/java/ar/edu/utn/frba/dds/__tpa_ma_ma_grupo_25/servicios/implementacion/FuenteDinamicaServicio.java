package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteDinamicaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteDinamica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeModificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudDeCreacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudDeModificacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICategoriaServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteDinamicaServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j // Logger
@Service
public class FuenteDinamicaServicio implements IFuenteDinamicaServicio {
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final IHechoRepositorioBD hechoRepositorio;
  private final IHechoServicio hechoServicio;
  private final ISolicitudDeCreacionServicio solicitudDeCreacionServicio;
  private final ICategoriaServicio categoriaServicio;
  private final ISolicitudDeCreacionRepositorioBD solicitudDeCreacionRepositorio;
  private final ISolicitudDeModificacionRepositorioBD solicitudDeModificacionRepositorio;

  public FuenteDinamicaServicio(
          IFuenteRepositorioBD fuenteRepositorio,
          IHechoRepositorioBD hechoRepositorio,
          IHechoServicio hechoServicio,
          ISolicitudDeCreacionServicio solicitudDeCreacionServicio,
          ICategoriaServicio categoriaServicio,
          ISolicitudDeCreacionRepositorioBD solicitudDeCreacionRepositorio,
          ISolicitudDeModificacionRepositorioBD solicitudDeModificacionRepositorio
  ) {
    this.fuenteRepositorio = fuenteRepositorio;
    this.hechoRepositorio = hechoRepositorio;
    this.hechoServicio = hechoServicio;
    this.solicitudDeCreacionServicio = solicitudDeCreacionServicio;
    this.categoriaServicio = categoriaServicio;
    this.solicitudDeCreacionRepositorio = solicitudDeCreacionRepositorio;
    this.solicitudDeModificacionRepositorio = solicitudDeModificacionRepositorio;

    fuenteRepositorio.findById(1L).orElseGet(() -> {
      FuenteDinamica fuente = new FuenteDinamica();
      fuente.setFechaDeCreacion(LocalDateTime.now());
      fuenteRepositorio.save(fuente);
      log.info("Fuente dinámica inicial creada id={}", fuente.getId());
      return fuente;
    });
  }



  @Override
  public FuenteDinamicaOutputDTO crear() {
    FuenteDinamica fuente = new FuenteDinamica();
    fuenteRepositorio.save(fuente);
    log.info("Fuente dinámica creada id={}", fuente.getId());
    return new FuenteDinamicaOutputDTO(
            fuente.getId(),
            LocalDateTime.now()//,
            //fuente.getHechosAsociados()
    );
  }

  @Override
  public List<FuenteDinamicaOutputDTO> encontrarTodas() {
    List<FuenteDinamica> fuentes = fuenteRepositorio.findAll();
    // Log de consulta comentado para producción
    // log.info("<CONSULTA> {} Fuentes disponibles", fuentes.size());
    return fuentes.stream()
            .map(FuenteDinamica::toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<HechoOutputDTO> hechosDeFuente(Long idFuente) {
    FuenteDinamica fuente = fuenteRepositorio.findById(idFuente)
            .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + idFuente));

    List<Hecho> hechos = fuente.getHechosAsociados().stream()
            .filter(Hecho::getVisible)
            .toList();

    // Log de consulta comentado para producción
    // log.info("<CONSULTA> Fuente {} respondió con {} hechos", idFuente, hechos.size());
    return hechos.stream().map(Hecho::toDTO).toList();
  }


  @Override
  @Transactional(readOnly = true)
  public HechoOutputDTO hechoPorId(Long idFuente, Long idHecho) {
    FuenteDinamica fuente = fuenteRepositorio.findById(idFuente)
            .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + idFuente));

    return fuente.getHechosAsociados().stream()
            .filter(h -> h.getId().equals(idHecho))
            .findFirst()
            .map(h -> {
              // Log de consulta comentado para producción
              // log.info("<CONSULTA> Fuente {} respondió con hecho de id {}", idFuente, idHecho);
              return h.toDTO();
            })
            .orElse(null);
  }

  @Override
  @Transactional
  public HechoOutputDTO cargarHecho(HechoInputDTO i) {
    Categoria categoria = categoriaServicio.findByNombre(i.getCategoria());
    if (categoria == null) {
      categoria = categoriaServicio.crear(i.getCategoria());
    }
    Hecho h = hechoServicio.crear(i.getTitulo(), i.getDescripcion(), categoria, i.getLatitud(), i.getLongitud(), i.getFechaDelHecho(), i.getIdContenido(), false);
    hechoRepositorio.save(h);
    solicitudDeCreacionServicio.crear(h, i.getIdContribuyente());
    log.info("Hecho creado id={} contribuyente={} multimediaId={}", h.getId(), i.getIdContribuyente(),
            h.getContenido() != null ? h.getContenido().getId() : "null");
    return h.toDTO();
  }

  @Override
  @Transactional
  public HechoOutputDTO modificarHecho(HechoModificacionDTO modificacion) {

    SolicitudDeCreacion s = solicitudDeCreacionServicio.encontrarPorIdHecho(modificacion.getIdHecho());
    if (solicitudDeCreacionServicio.fueCreadaPor(s, modificacion.getIdContribuyente()) && !solicitudDeCreacionServicio.estaVencida(s)) {
      log.info("Hecho id={} modificado por contribuyente={}", modificacion.getIdHecho(), modificacion.getIdContribuyente());
      return hechoServicio.modificarHecho(modificacion);
    }
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public EstadisticasFuenteDinamicaDTO obtenerEstadisticas() {
    // Log de solicitud comentado para producción
    // log.info("<ESTADISTICAS> Solicitando estadísticas de FuenteDinamica");

    try {
      // Estadísticas de solicitudes de creación
      List<SolicitudDeCreacion> solicitudesCreacion = solicitudDeCreacionRepositorio.findAll();
      long solCreacionTotal = solicitudesCreacion.size();
      long solCreacionAceptadas = solicitudesCreacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.ACEPTADA)
              .count();
      long solCreacionRechazadas = solicitudesCreacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.RECHAZADA)
              .count();
      long solCreacionPendientes = solicitudesCreacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE)
              .count();
      long solCreacionPendientesMod = solicitudesCreacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION)
              .count();

      // Estadísticas de solicitudes de modificación
      List<SolicitudDeModificacion> solicitudesModificacion = solicitudDeModificacionRepositorio.findAll();
      long solModificacionTotal = solicitudesModificacion.size();
      long solModificacionAceptadas = solicitudesModificacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.ACEPTADA)
              .count();
      long solModificacionRechazadas = solicitudesModificacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.RECHAZADA)
              .count();
      long solModificacionPendientes = solicitudesModificacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE)
              .count();
      long solModificacionPendientesMod = solicitudesModificacion.stream()
              .filter(s -> s.estadoActual() != null &&
                      s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION)
              .count();

      EstadisticasFuenteDinamicaDTO estadisticas = new EstadisticasFuenteDinamicaDTO(
              solCreacionAceptadas, // hechosCreados = solicitudes de creación aceptadas
              solModificacionAceptadas, // hechosModificados = solicitudes de modificación aceptadas
              solCreacionTotal,
              solCreacionAceptadas,
              solCreacionRechazadas,
              solCreacionPendientes,
              solCreacionPendientesMod,
              solModificacionTotal,
              solModificacionAceptadas,
              solModificacionRechazadas,
              solModificacionPendientes,
              solModificacionPendientesMod
      );

      // Log de estadísticas comentado para producción
      // log.info("<ESTADISTICAS> FuenteDinamica - Hechos C:{}/M:{}, Sol.Creación: {}/{}/{}/{}, Sol.Modificación: {}/{}/{}/{}",
      //         solCreacionAceptadas, solModificacionAceptadas,
      //         solCreacionTotal, solCreacionAceptadas, solCreacionRechazadas, solCreacionPendientes,
      //         solModificacionTotal, solModificacionAceptadas, solModificacionRechazadas, solModificacionPendientes);

      return estadisticas;

    } catch (Exception e) {
      log.error("<ERROR> Error al obtener estadísticas de FuenteDinamica: {}", e.getMessage());
      return new EstadisticasFuenteDinamicaDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }
  }
}
