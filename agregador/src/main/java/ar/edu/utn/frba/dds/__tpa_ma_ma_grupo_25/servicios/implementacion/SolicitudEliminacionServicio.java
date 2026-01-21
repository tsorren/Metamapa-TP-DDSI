package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeEliminacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IDetectorDeSpam;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.INotificacionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SolicitudEliminacionServicio implements ISolicitudEliminacionServicio {
  private final ISolicitudEliminacionRepositorioBD solicitudesRepositorio;
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final IHechoRepositorioBD hechoRepositorio;
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;
  private final IDetectorDeSpam detectorDeSpam;
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final INotificacionServicio notificacionServicio;

  public SolicitudEliminacionServicio(ISolicitudEliminacionRepositorioBD s, IAdministradorRepositorioBD a, IHechoRepositorioBD h, IContribuyenteRepositorioBD c, IDetectorDeSpam detectorDeSpam, IFuenteRepositorioBD fuenteRepositorio, INotificacionServicio notificacionServicio) {
    this.solicitudesRepositorio = s;
    this.administradorRepositorio = a;
    this.hechoRepositorio = h;
    this.contribuyenteRepositorio = c;
    this.detectorDeSpam = detectorDeSpam;
    this.fuenteRepositorio = fuenteRepositorio;
    this.notificacionServicio = notificacionServicio;
  }

  @Override
  @Transactional(readOnly = true)
  public List<SolicitudDeEliminacionOutputDTO> encontrarTodas(EstadoAtencionSolicitud estado) {
    // En SolicitudDeEliminacion, el estado NO es un historial, es un campo único
    // Por lo tanto, findByEstadoAtencionSolicitud ya busca por el estado actual
    List<SolicitudDeEliminacion> solicitudes = (estado == null)
            ? solicitudesRepositorio.findAll()
            : solicitudesRepositorio.findByEstadoAtencionSolicitud(estado);
    
    log.info("<CONSULTA> {} Solicitudes de Eliminacion con estado ACTUAL {}", solicitudes.size(), estado == null ? "TODOS" : estado);
    return solicitudes.stream().map(SolicitudDeEliminacion::toDTO).toList();
  }

  @Override
  @Transactional
  public SolicitudDeEliminacionOutputDTO procesarSolicitud(Long idSolicitud, Long idResponsable, EstadoAtencionSolicitud estado, String justificacion) {
    SolicitudDeEliminacion s = solicitudesRepositorio.findById(idSolicitud).orElseThrow();
    var administrador = administradorRepositorio.findById(idResponsable).orElseThrow();

    if (estado == EstadoAtencionSolicitud.ACEPTADA) {
      s.marcarAceptada(administrador);
      s.marcarAceptada(administrador);
      this.ocultarEnFuente(s)
              .doOnError(e -> log.error(
                      "Error al ocultar hecho {} en fuente: {}",
                      s.getHecho().getId(),
                      e.getMessage()))
              .subscribe(); // ejecutar request

      if(s.getContribuyente() != null) {
      // Notificar al contribuyente
      String mensaje = String.format("Tu solicitud de eliminación del hecho '%s' fue ACEPTADA por el administrador %s.",
                                     s.getHecho().getTitulo(),
                                     administrador.getUsername());
        notificacionServicio.notificarAContribuyente(
                s.getContribuyente().getId(),
                mensaje,
                TipoNotificacion.SOLICITUD_ACEPTADA,
                s.getId(),
                TipoSolicitud.ELIMINACION
        );
      }
    } else if (estado == EstadoAtencionSolicitud.RECHAZADA) {
      //TODO: no deberiamos permitir rechazar sin justificacion.
      s.marcarRechazada(administrador, justificacion);

      // Notificar al contribuyente
      String mensaje = String.format("Tu solicitud de eliminación del hecho '%s' fue RECHAZADA por el administrador %s. Justificación: %s",
                                     s.getHecho().getTitulo(),
                                     administrador.getUsername(),
                                     justificacion != null ? justificacion : "Sin justificación");

      if(s.getContribuyente() != null) {
        notificacionServicio.notificarAContribuyente(
                s.getContribuyente().getId(),
                mensaje,
                TipoNotificacion.SOLICITUD_RECHAZADA,
                s.getId(),
                TipoSolicitud.ELIMINACION
        );
      }
    }
    return s.toDTO();
  }

  @Override
  @Transactional
  public SolicitudDeEliminacionOutputDTO solicitarEliminacion(Long idHecho, String justficacion, Long idContribuyente) {
    var hecho = hechoRepositorio.findById(idHecho)
            .orElseThrow(() -> new RuntimeException("Hecho no encontrado con id: " + idHecho));
    var contribuyente = idContribuyente != null
            ? contribuyenteRepositorio.findById(idContribuyente).orElse(null)
            : null;

    SolicitudDeEliminacion s = new SolicitudDeEliminacion(hecho, justficacion, contribuyente);
    solicitudesRepositorio.save(s);

    // Notificar a todos los administradores
    String mensaje = String.format("Nueva solicitud de eliminación del hecho '%s' por el contribuyente '%s'",
                                   hecho.getTitulo(),
            contribuyente != null ? contribuyente.getNombre() : "Anónimo" );
    notificacionServicio.notificarATodosLosAdministradores(
            mensaje,
            TipoNotificacion.SOLICITUD_PENDIENTE,
            s.getId(),
            TipoSolicitud.ELIMINACION
    );

    return s.toDTO();
  }

  @Override
  @Transactional
  public void procesarSpam(LocalDateTime ultimoProcesamiento) {
    List<SolicitudDeEliminacion> solicitudesAProcesar = solicitudesRepositorio.findByEstadoAtencionSolicitud(EstadoAtencionSolicitud.PENDIENTE).stream().filter(s -> s.getFechaCreacion().isAfter(ultimoProcesamiento)).toList();
    solicitudesAProcesar.forEach(this::revisarSpam);
  }

  @Transactional
  public void revisarSpam(SolicitudDeEliminacion s) {
    if (detectorDeSpam.esSpam(s.getJustificacion()))
      //TODO:deberia haber un administrador "System" o algo que asignar aca
      s.marcarRechazada(null, "Es Spam");
  }

  private Mono<Void> ocultarEnFuente(SolicitudDeEliminacion s) {
    WebClient webClient = s.getHecho().getFuente().getModulo().getApi();
    return webClient.patch().uri(uriBuilder -> uriBuilder
                    .path("/ocultar/{id}")
                    .build(s.getHecho().getIdExterno()))
            .retrieve()
            .bodyToMono(Void.class);
  }
}
