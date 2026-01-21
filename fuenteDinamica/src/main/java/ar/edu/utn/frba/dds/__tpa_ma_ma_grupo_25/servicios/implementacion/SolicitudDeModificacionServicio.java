package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeModificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeModificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteDinamicaServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.INotificacionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeModificacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SolicitudDeModificacionServicio implements ISolicitudDeModificacionServicio {
  private final ISolicitudDeModificacionRepositorioBD solicitudesRepositorio;
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;

  private final Integer plazoDeModificacionEnDias;
  private final IHechoRepositorioBD hechoRepositorio;
  private final IFuenteDinamicaServicio fuenteDinamicaServicio;
  private final INotificacionServicio notificacionServicio;

  public SolicitudDeModificacionServicio(
          ISolicitudDeModificacionRepositorioBD solicitudesRepositorio,
          IAdministradorRepositorioBD administradorRepositorio,
          IContribuyenteRepositorioBD contribuyenteRepositorio,
          @Value("${cron.plazoDeModificacionEnDias}") Integer plazo,
          IHechoRepositorioBD hechoRepositorio,
          IFuenteDinamicaServicio fuenteDinamicaServicio,
          INotificacionServicio notificacionServicio){
    this.solicitudesRepositorio = solicitudesRepositorio;
    this.administradorRepositorio = administradorRepositorio;
    this.contribuyenteRepositorio = contribuyenteRepositorio;
    this.plazoDeModificacionEnDias = plazo;
    this.hechoRepositorio = hechoRepositorio;
    this.fuenteDinamicaServicio = fuenteDinamicaServicio;
    this.notificacionServicio = notificacionServicio;
  }

  @Override
  @Transactional
  public SolicitudDeModificacionOutputDTO crear(HechoModificacionDTO i) {
    Contribuyente contribuyente = contribuyenteRepositorio.findById(i.getIdContribuyente()).orElseThrow();
    Hecho hecho = hechoRepositorio.findById(i.getIdHecho()).orElseThrow();
    SolicitudDeModificacion s = new SolicitudDeModificacion(hecho, i ,contribuyente);
    solicitudesRepositorio.save(s);
    log.info("<CREADO> Solicitud de Modificacion con id {}", s.getId());

    // Notificar a todos los administradores
    String mensaje = String.format("Nueva solicitud de modificación del hecho '%s' por el contribuyente '%s'",
                                   hecho.getTitulo(),
                                   contribuyente.getNombre());
    notificacionServicio.notificarATodosLosAdministradores(
            mensaje,
            TipoNotificacion.SOLICITUD_PENDIENTE,
            s.getId(),
            TipoSolicitud.MODIFICACION
    );

    return s.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public List<SolicitudDeModificacionOutputDTO> encontrarTodas(TipoEstadoRevision estado) {
    List<SolicitudDeModificacion> todasLasSolicitudes = solicitudesRepositorio.findAll();
    
    // Filtrar por estado actual (no histórico)
    List<SolicitudDeModificacion> solicitudes = (estado == null)
            ? todasLasSolicitudes
            : todasLasSolicitudes.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == estado)
                    .toList();
    
    log.info("<CONSULTA> {} Solicitudes de Modificacion con estado actual {}", solicitudes.size(), estado == null ? "TODOS" : estado);

    return solicitudes.stream().map(SolicitudDeModificacion::toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public SolicitudDeModificacionOutputDTO encontrarPorId(Long identificador) {
    return solicitudesRepositorio.findById(identificador).orElseThrow().toDTO();
  }

  @Override
  @Transactional
  public SolicitudDeModificacionOutputDTO procesar(Long idSolicitud, Long idResponsable, TipoEstadoRevision nuevoEstado, String feedback) {
    SolicitudDeModificacion s = solicitudesRepositorio.findById(idSolicitud).orElseThrow();
    var administrador = administradorRepositorio.findById(idResponsable).orElseThrow();
    s.procesar(administrador, nuevoEstado, feedback);
    solicitudesRepositorio.save(s);

    if (nuevoEstado == TipoEstadoRevision.ACEPTADA) {
      fuenteDinamicaServicio.modificarHecho(s.getModificacion());

      // Notificar al contribuyente
      String mensaje = String.format("Tu solicitud de modificación del hecho '%s' fue ACEPTADA por el administrador %s. Feedback: %s",
                                     s.getHecho().getTitulo(),
                                     administrador.getUsername(),
                                     feedback);
      notificacionServicio.notificarAContribuyente(
              s.getContribuyente().getId(),
              mensaje,
              TipoNotificacion.SOLICITUD_ACEPTADA,
              s.getId(),
              TipoSolicitud.MODIFICACION
      );
    } else if (nuevoEstado == TipoEstadoRevision.RECHAZADA) {
      // Notificar al contribuyente
      String mensaje = String.format("Tu solicitud de modificación del hecho '%s' fue RECHAZADA por el administrador %s. Feedback: %s",
                                     s.getHecho().getTitulo(),
                                     administrador.getUsername(),
                                     feedback);
      notificacionServicio.notificarAContribuyente(
              s.getContribuyente().getId(),
              mensaje,
              TipoNotificacion.SOLICITUD_RECHAZADA,
              s.getId(),
              TipoSolicitud.MODIFICACION
      );
    } else if (nuevoEstado == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION) {
      // Notificar al contribuyente
      String mensaje = String.format("Tu solicitud de modificación del hecho '%s' está PENDIENTE DE MODIFICACIÓN. Administrador: %s. Feedback: %s",
                                     s.getHecho().getTitulo(),
                                     administrador.getUsername(),
                                     feedback);
      notificacionServicio.notificarAContribuyente(
              s.getContribuyente().getId(),
              mensaje,
              TipoNotificacion.SOLICITUD_PENDIENTE_MODIFICACION,
              s.getId(),
              TipoSolicitud.MODIFICACION
      );
    }

    log.info("<PROCESADA> Solicitud con id {}, estado: {}", s.getId(), nuevoEstado);
    return s.toDTO();
  }

  private List<SolicitudDeModificacion> pendientesDeModificacion() {
    return solicitudesRepositorio.findByEstados_TipoEstado(TipoEstadoRevision.PENDIENTE_DE_MODIFICACION);
  }

  private List<SolicitudDeModificacion> solicitudesVencidas() {
    return this.pendientesDeModificacion()
            .stream()
            .filter(this::estaVencida)
            .collect(Collectors.toList());
  }

  public void procesarPendientesDeModificacion() {
    this.solicitudesVencidas()
            .forEach(s -> this.procesar(
                    s.getId(),
                    s.estadoAnterior().getAdministradorResponsable().getId(),
                    TipoEstadoRevision.RECHAZADA,
                    "Se venció el plazo de modificación")
            );
  }

  @Override
  public SolicitudDeModificacion encontrarPorIdHecho(Long idHecho) {
    return solicitudesRepositorio.findByHecho_Id(idHecho);
  }

  @Override
  public Boolean estaVencida(SolicitudDeModificacion s) {
    return LocalDateTime.now().isAfter(s.ultimaFechaProcesamiento().plusDays(plazoDeModificacionEnDias));
  }

  @Override
  public Boolean fueCreadaPor(SolicitudDeModificacion s, Long idContribuyente) {
    return s.getContribuyente().getId().equals(idContribuyente);
  }

}
