package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteDinamica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IAdministradorRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContribuyenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudDeCreacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.INotificacionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SolicitudDeCreacionServicio implements ISolicitudDeCreacionServicio {

  private final ISolicitudDeCreacionRepositorioBD solicitudesRepositorio;
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final INotificacionServicio notificacionServicio;
  private final Integer plazoDeModificacionEnDias;
  private final HechoServicio hechoServicio;

  public SolicitudDeCreacionServicio(
          ISolicitudDeCreacionRepositorioBD s,
          IAdministradorRepositorioBD a,
          IContribuyenteRepositorioBD c,
          IFuenteRepositorioBD f,
          INotificacionServicio notificacionServicio,
          @Value("${cron.plazoDeModificacionEnDias}") Integer plazo,
          HechoServicio hechoServicio) {
    this.solicitudesRepositorio = s;
    this.administradorRepositorio = a;
    this.contribuyenteRepositorio = c;
    this.fuenteRepositorio = f;
    this.notificacionServicio = notificacionServicio;
    this.plazoDeModificacionEnDias = plazo;
    this.hechoServicio = hechoServicio;
  }

  @Override
  @Transactional
  public SolicitudDeCreacion crear(Hecho hecho, Long idContribuyente) {
    Contribuyente contribuyente = contribuyenteRepositorio.findById(idContribuyente).orElseThrow();
    SolicitudDeCreacion s = new SolicitudDeCreacion(hecho, contribuyente);
    contribuyente.agregarHecho(hecho);
    hecho.setVisible(false);
    solicitudesRepositorio.save(s);
    log.info("<CREADO> Solicitud de Creacion con id {}", s.getId());

    // Notificar a todos los administradores
    String mensaje = String.format("Nueva solicitud de creación del hecho '%s' por el contribuyente '%s'",
                                   hecho.getTitulo(),
                                   contribuyente.getNombre());
    notificacionServicio.notificarATodosLosAdministradores(
            mensaje,
            TipoNotificacion.SOLICITUD_PENDIENTE,
            s.getId(),
            TipoSolicitud.CREACION
    );

    return s;
  }

  @Override
  @Transactional(readOnly = true)
  public List<SolicitudDeCreacionOutputDTO> encontrarTodas(TipoEstadoRevision estado) {
    List<SolicitudDeCreacion> todasLasSolicitudes = solicitudesRepositorio.findAll();
    
    // Filtrar por estado actual (no histórico)
    List<SolicitudDeCreacion> solicitudes = (estado == null)
            ? todasLasSolicitudes
            : todasLasSolicitudes.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == estado)
                    .toList();

    log.info("<CONSULTA> {} Solicitudes de Creacion con estado actual {}", solicitudes.size(), estado == null ? "TODOS" : estado);
    return solicitudes.stream().map(SolicitudDeCreacion::toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public SolicitudDeCreacionOutputDTO encontrarPorId(Long identificador) {
    return solicitudesRepositorio.findById(identificador).orElseThrow().toDTO();
  }

  @Override
  @Transactional
  public SolicitudDeCreacionOutputDTO procesar(Long idSolicitud, Long idResponsable, TipoEstadoRevision nuevoEstado, String feedback) {
    SolicitudDeCreacion s = solicitudesRepositorio.findById(idSolicitud).orElseThrow();
    var administrador = administradorRepositorio.findById(idResponsable).orElseThrow();
    s.procesar(administrador, nuevoEstado, feedback);
    solicitudesRepositorio.save(s);

    if (nuevoEstado == TipoEstadoRevision.ACEPTADA) {
      // Recuperamos la fuente con id fijo (ej. 1)
      FuenteDinamica fuente = fuenteRepositorio.findById(1L).orElseThrow();

      // Asociamos el hecho a la fuente
      fuente.getHechosAsociados().add(s.getHecho());

      // Persistimos la relación
      fuenteRepositorio.save(fuente);

      // El hecho pasa a ser visible
      s.getHecho().setVisible(true);
      hechoServicio.save(s.getHecho());

      if(s.getContribuyente() != null) {
        // Notificar al contribuyente
        String mensaje = String.format("Tu solicitud de creación del hecho '%s' fue ACEPTADA por el administrador %s. Feedback: %s",
                s.getHecho().getTitulo(),
                administrador.getUsername(),
                feedback);
        notificacionServicio.notificarAContribuyente(
                s.getContribuyente().getId(),
                mensaje,
                TipoNotificacion.SOLICITUD_ACEPTADA,
                s.getId(),
                TipoSolicitud.CREACION
        );
      }


    } else if (nuevoEstado == TipoEstadoRevision.RECHAZADA) {
      s.getHecho().setVisible(false);
      hechoServicio.save(s.getHecho());

      if(s.getContribuyente() != null) {
        // Notificar al contribuyente
        String mensaje = String.format("Tu solicitud de creación del hecho '%s' fue RECHAZADA por el administrador %s. Feedback: %s",
                s.getHecho().getTitulo(),
                administrador.getUsername(),
                feedback);
        notificacionServicio.notificarAContribuyente(
                s.getContribuyente().getId(),
                mensaje,
                TipoNotificacion.SOLICITUD_RECHAZADA,
                s.getId(),
                TipoSolicitud.CREACION
        );
      }
    } else if (nuevoEstado == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION) {

      if(s.getContribuyente() != null) {
        // Notificar al contribuyente
        String mensaje = String.format("Tu solicitud de creación del hecho '%s' está PENDIENTE DE MODIFICACIÓN. Administrador: %s. Feedback: %s",
                s.getHecho().getTitulo(),
                administrador.getUsername(),
                feedback);
        notificacionServicio.notificarAContribuyente(
                s.getContribuyente().getId(),
                mensaje,
                TipoNotificacion.SOLICITUD_PENDIENTE_MODIFICACION,
                s.getId(),
                TipoSolicitud.CREACION
        );
      }
    }

    log.info("<PROCESADA> Solicitud con id {}, estado: {}", s.getId(), nuevoEstado);
    return s.toDTO();
  }


  @Override
  public SolicitudDeCreacion encontrarPorIdHecho(Long idHecho) {
    return solicitudesRepositorio.findByHecho_Id(idHecho);
  }

  private List<SolicitudDeCreacion> pendientesDeModificacion() {
    return solicitudesRepositorio.findByEstados_TipoEstado(TipoEstadoRevision.PENDIENTE_DE_MODIFICACION);
  }

  private List<SolicitudDeCreacion> solicitudesVencidas() {
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
  public Boolean estaVencida(SolicitudDeCreacion s) {
    LocalDateTime ultimaFecha = s.ultimaFechaProcesamiento();
    if (ultimaFecha == null) {
      return false; // Si no ha sido procesada aún, no puede estar vencida
    }
    return LocalDateTime.now().isAfter(ultimaFecha.plusDays(plazoDeModificacionEnDias));
  }

  @Override
  public Boolean fueCreadaPor(SolicitudDeCreacion s, Long idContribuyente) {
    return s.getContribuyente().getId().equals(idContribuyente);
  }
}
