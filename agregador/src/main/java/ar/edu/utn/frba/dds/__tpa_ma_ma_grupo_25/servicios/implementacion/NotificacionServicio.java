package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.NotificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.Notificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoNotificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.TipoSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IAdministradorRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContribuyenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.INotificacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.INotificacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificacionServicio implements INotificacionServicio {

  private final INotificacionRepositorioBD notificacionRepositorio;
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;

  public NotificacionServicio(
          INotificacionRepositorioBD notificacionRepositorio,
          IAdministradorRepositorioBD administradorRepositorio,
          IContribuyenteRepositorioBD contribuyenteRepositorio) {
    this.notificacionRepositorio = notificacionRepositorio;
    this.administradorRepositorio = administradorRepositorio;
    this.contribuyenteRepositorio = contribuyenteRepositorio;
  }

  @Override
  @Transactional
  public void notificarATodosLosAdministradores(String mensaje, TipoNotificacion tipo, Long solicitudId, TipoSolicitud tipoSolicitud) {
    List<Administrador> administradores = administradorRepositorio.findAll();

    List<Notificacion> notificaciones = administradores.stream()
            .map(admin -> new Notificacion(mensaje, tipo, admin, solicitudId, tipoSolicitud))
            .collect(Collectors.toList());

    notificacionRepositorio.saveAll(notificaciones);
    // Log de auditoría - mantener para trazabilidad
    log.info("Notificaciones creadas: {} admins, tipo={}, solicitud={}", notificaciones.size(), tipoSolicitud, solicitudId);
  }

  @Override
  @Transactional
  public void notificarAContribuyente(Long idContribuyente, String mensaje, TipoNotificacion tipo, Long solicitudId, TipoSolicitud tipoSolicitud) {
    Contribuyente contribuyente = contribuyenteRepositorio.findById(idContribuyente)
            .orElseThrow(() -> new RuntimeException("Contribuyente no encontrado con id: " + idContribuyente));

    Notificacion notificacion = new Notificacion(mensaje, tipo, contribuyente, solicitudId, tipoSolicitud);
    notificacionRepositorio.save(notificacion);

    // Log de auditoría
    log.info("Notificación creada: contribuyente={}, tipo={}, solicitud={}", idContribuyente, tipoSolicitud, solicitudId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificacionOutputDTO> obtenerNotificacionesDeAdministrador(Long idAdministrador) {
    Administrador administrador = administradorRepositorio.findById(idAdministrador)
            .orElseThrow(() -> new RuntimeException("Administrador no encontrado con id: " + idAdministrador));

    List<Notificacion> notificaciones = notificacionRepositorio.findByAdministradorOrderByFechaCreacionDesc(administrador);
    return notificaciones.stream().map(Notificacion::toDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificacionOutputDTO> obtenerNotificacionesDeContribuyente(Long idContribuyente) {
    Contribuyente contribuyente = contribuyenteRepositorio.findById(idContribuyente)
            .orElseThrow(() -> new RuntimeException("Contribuyente no encontrado con id: " + idContribuyente));

    List<Notificacion> notificaciones = notificacionRepositorio.findByContribuyenteOrderByFechaCreacionDesc(contribuyente);
    return notificaciones.stream().map(Notificacion::toDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void marcarComoLeida(Long idNotificacion) {
    Notificacion notificacion = notificacionRepositorio.findById(idNotificacion)
            .orElseThrow(() -> new RuntimeException("Notificación no encontrada con id: " + idNotificacion));

    notificacion.marcarComoLeida();
    notificacionRepositorio.save(notificacion);
    // Log comentado para producción
    // log.info("Notificación {} marcada como leída", idNotificacion);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificacionOutputDTO> obtenerNotificacionesNoLeidasDeAdministrador(Long idAdministrador) {
    // Log comentado para producción
    // log.info("[Agregador-Servicio] Buscando notificaciones NO LEÍDAS para admin id: {}", idAdministrador);

    try {
      Administrador administrador = administradorRepositorio.findById(idAdministrador).orElse(null);

      if (administrador == null) {
        log.warn("Admin id={} no encontrado al buscar notificaciones", idAdministrador);
        return new ArrayList<>();
      }

      // log.info("[Agregador-Servicio] Administrador encontrado: {}", administrador.getId());

      List<Notificacion> notificaciones = notificacionRepositorio.findByAdministradorAndLeida(administrador, false);

      // log.info("[Agregador-Servicio] Encontradas {} notificaciones NO LEÍDAS", notificaciones.size());

      return notificaciones.stream().map(Notificacion::toDTO).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Error al obtener notificaciones para admin {}: {}", idAdministrador, e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificacionOutputDTO> obtenerNotificacionesNoLeidasDeContribuyente(Long idContribuyente) {
    // Log comentado para producción
    // log.info("[Agregador-Servicio] Buscando notificaciones NO LEÍDAS para contribuyente id: {}", idContribuyente);

    try {
      Contribuyente contribuyente = contribuyenteRepositorio.findById(idContribuyente).orElse(null);

      if (contribuyente == null) {
        log.warn("Contribuyente id={} no encontrado al buscar notificaciones", idContribuyente);
        return new ArrayList<>();
      }

      // log.info("[Agregador-Servicio] Contribuyente encontrado: {}", contribuyente.getId());

      List<Notificacion> notificaciones = notificacionRepositorio.findByContribuyenteAndLeida(contribuyente, false);

      // log.info("[Agregador-Servicio] Encontradas {} notificaciones NO LEÍDAS", notificaciones.size());

      return notificaciones.stream().map(Notificacion::toDTO).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Error al obtener notificaciones para contribuyente {}: {}", idContribuyente, e.getMessage());
      return new ArrayList<>();
    }
  }
}

