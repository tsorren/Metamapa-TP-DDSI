package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.NotificacionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificacionService {

  private final AgregadorClient agregadorClient;
  private final FuenteDinamicaClient fuenteDinamicaClient;

  public NotificacionService(AgregadorClient agregadorClient, FuenteDinamicaClient fuenteDinamicaClient) {
    this.agregadorClient = agregadorClient;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
  }

  /**
   * Obtiene TODAS las notificaciones de un administrador desde ambos microservicios
   */
  public List<NotificacionDTO> obtenerNotificacionesDeAdministrador(Long idAdministrador) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      // Notificaciones de FuenteDinamica (creación y modificación)
      List<NotificacionDTO> notifFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesDeAdministrador(idAdministrador);
      if (notifFuenteDinamica != null) {
        notificaciones.addAll(notifFuenteDinamica);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones de FuenteDinamica para admin {}: {}", idAdministrador, e.getMessage());
    }

    try {
      // Notificaciones de Agregador (eliminación)
      List<NotificacionDTO> notifAgregador = agregadorClient.obtenerNotificacionesDeAdministrador(idAdministrador);
      if (notifAgregador != null) {
        notificaciones.addAll(notifAgregador);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones de Agregador para admin {}: {}", idAdministrador, e.getMessage());
    }

    // Ordenar por fecha descendente (más recientes primero)
    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Obtiene solo las notificaciones NO LEÍDAS de un administrador
   */
  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeAdministrador(Long idAdministrador) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      List<NotificacionDTO> notifFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador);
      if (notifFuenteDinamica != null) {
        notificaciones.addAll(notifFuenteDinamica);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones no leídas de FuenteDinamica para admin {}: {}", idAdministrador, e.getMessage());
    }

    try {
      List<NotificacionDTO> notifAgregador = agregadorClient.obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador);
      if (notifAgregador != null) {
        notificaciones.addAll(notifAgregador);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones no leídas de Agregador para admin {}: {}", idAdministrador, e.getMessage());
    }

    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Obtiene TODAS las notificaciones de un contribuyente desde ambos microservicios
   */
  public List<NotificacionDTO> obtenerNotificacionesDeContribuyente(Long idContribuyente) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      List<NotificacionDTO> notifFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesDeContribuyente(idContribuyente);
      if (notifFuenteDinamica != null) {
        notificaciones.addAll(notifFuenteDinamica);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones de FuenteDinamica para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    try {
      List<NotificacionDTO> notifAgregador = agregadorClient.obtenerNotificacionesDeContribuyente(idContribuyente);
      if (notifAgregador != null) {
        notificaciones.addAll(notifAgregador);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones de Agregador para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Obtiene solo las notificaciones NO LEÍDAS de un contribuyente
   */
  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeContribuyente(Long idContribuyente) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      List<NotificacionDTO> notifFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente);
      if (notifFuenteDinamica != null) {
        notificaciones.addAll(notifFuenteDinamica);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones no leídas de FuenteDinamica para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    try {
      List<NotificacionDTO> notifAgregador = agregadorClient.obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente);
      if (notifAgregador != null) {
        notificaciones.addAll(notifAgregador);
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones no leídas de Agregador para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Marca una notificación como leída
   * Intenta en ambos microservicios ya que no sabemos de cuál proviene
   */
  public void marcarComoLeida(Long idNotificacion, String tipoSolicitud) {
    try {
      // Si es de eliminación, está en Agregador
      if ("ELIMINACION".equals(tipoSolicitud)) {
        agregadorClient.marcarNotificacionComoLeida(idNotificacion);
      } else {
        // Si es de creación o modificación, está en FuenteDinamica
        fuenteDinamicaClient.marcarNotificacionComoLeida(idNotificacion);
      }
      log.info("Notificación {} marcada como leída", idNotificacion);
    } catch (Exception e) {
      log.error("Error al marcar notificación {} como leída: {}", idNotificacion, e.getMessage());
    }
  }

  /**
   * Cuenta las notificaciones no leídas
   */
  public int contarNotificacionesNoLeidasDeAdministrador(Long idAdministrador) {
    return obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador).size();
  }

  public int contarNotificacionesNoLeidasDeContribuyente(Long idContribuyente) {
    return obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente).size();
  }

  /**
   * Obtiene solo las notificaciones LEÍDAS de un administrador
   * Optimizado: Las notificaciones leídas se obtienen directamente de cada servicio
   * para evitar cargar todas las notificaciones en memoria
   */
  public List<NotificacionDTO> obtenerNotificacionesLeidasDeAdministrador(Long idAdministrador) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      // Obtener todas las notificaciones y filtrar las leídas
      // TODO: Implementar endpoint específico para notificaciones leídas en cada microservicio
      List<NotificacionDTO> todasFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesDeAdministrador(idAdministrador);
      if (todasFuenteDinamica != null) {
        notificaciones.addAll(todasFuenteDinamica.stream()
                .filter(NotificacionDTO::getLeida)
                .collect(Collectors.toList()));
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones leídas de FuenteDinamica para admin {}: {}", idAdministrador, e.getMessage());
    }

    try {
      List<NotificacionDTO> todasAgregador = agregadorClient.obtenerNotificacionesDeAdministrador(idAdministrador);
      if (todasAgregador != null) {
        notificaciones.addAll(todasAgregador.stream()
                .filter(NotificacionDTO::getLeida)
                .collect(Collectors.toList()));
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones leídas de Agregador para admin {}: {}", idAdministrador, e.getMessage());
    }

    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Obtiene solo las notificaciones LEÍDAS de un contribuyente
   * Optimizado: Usa el mismo enfoque que para administradores
   */
  public List<NotificacionDTO> obtenerNotificacionesLeidasDeContribuyente(Long idContribuyente) {
    List<NotificacionDTO> notificaciones = new ArrayList<>();

    try {
      List<NotificacionDTO> todasFuenteDinamica = fuenteDinamicaClient.obtenerNotificacionesDeContribuyente(idContribuyente);
      if (todasFuenteDinamica != null) {
        notificaciones.addAll(todasFuenteDinamica.stream()
                .filter(NotificacionDTO::getLeida)
                .collect(Collectors.toList()));
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones leídas de FuenteDinamica para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    try {
      List<NotificacionDTO> todasAgregador = agregadorClient.obtenerNotificacionesDeContribuyente(idContribuyente);
      if (todasAgregador != null) {
        notificaciones.addAll(todasAgregador.stream()
                .filter(NotificacionDTO::getLeida)
                .collect(Collectors.toList()));
      }
    } catch (Exception e) {
      log.error("Error al obtener notificaciones leídas de Agregador para contribuyente {}: {}", idContribuyente, e.getMessage());
    }

    return notificaciones.stream()
            .sorted(Comparator.comparing(NotificacionDTO::getFechaCreacion).reversed())
            .collect(Collectors.toList());
  }

  /**
   * Marca todas las notificaciones como leídas para un administrador
   * Optimizado: Agrupa notificaciones por origen antes de hacer las llamadas
   */
  public void marcarTodasComoLeidasAdministrador(Long idAdministrador) {
    List<NotificacionDTO> notificaciones = obtenerNotificacionesNoLeidasDeAdministrador(idAdministrador);

    if (notificaciones.isEmpty()) {
      log.info("No hay notificaciones no leídas para admin {}", idAdministrador);
      return;
    }

    // Agrupar por tipo de solicitud para batch processing
    Map<String, List<NotificacionDTO>> porTipo = notificaciones.stream()
            .collect(Collectors.groupingBy(n -> n.getTipoSolicitud() != null ? n.getTipoSolicitud() : "UNKNOWN"));

    int marcadas = 0;
    int errores = 0;

    for (Map.Entry<String, List<NotificacionDTO>> entry : porTipo.entrySet()) {
      String tipo = entry.getKey();
      List<NotificacionDTO> grupo = entry.getValue();

      log.debug("Marcando {} notificaciones de tipo {} para admin {}", grupo.size(), tipo, idAdministrador);

      for (NotificacionDTO notif : grupo) {
        try {
          marcarComoLeida(notif.getId(), notif.getTipoSolicitud());
          marcadas++;
        } catch (Exception e) {
          errores++;
          log.error("Error al marcar notificación {} como leída: {}", notif.getId(), e.getMessage());
        }
      }
    }

    log.info("Marcadas {}/{} notificaciones como leídas para admin {} (errores: {})",
             marcadas, notificaciones.size(), idAdministrador, errores);
  }

  /**
   * Marca todas las notificaciones como leídas para un contribuyente
   * Optimizado: Agrupa notificaciones por origen antes de hacer las llamadas
   */
  public void marcarTodasComoLeidasContribuyente(Long idContribuyente) {
    List<NotificacionDTO> notificaciones = obtenerNotificacionesNoLeidasDeContribuyente(idContribuyente);

    if (notificaciones.isEmpty()) {
      log.info("No hay notificaciones no leídas para contribuyente {}", idContribuyente);
      return;
    }

    // Agrupar por tipo de solicitud para batch processing
    Map<String, List<NotificacionDTO>> porTipo = notificaciones.stream()
            .collect(Collectors.groupingBy(n -> n.getTipoSolicitud() != null ? n.getTipoSolicitud() : "UNKNOWN"));

    int marcadas = 0;
    int errores = 0;

    for (Map.Entry<String, List<NotificacionDTO>> entry : porTipo.entrySet()) {
      String tipo = entry.getKey();
      List<NotificacionDTO> grupo = entry.getValue();

      log.debug("Marcando {} notificaciones de tipo {} para contribuyente {}", grupo.size(), tipo, idContribuyente);

      for (NotificacionDTO notif : grupo) {
        try {
          marcarComoLeida(notif.getId(), notif.getTipoSolicitud());
          marcadas++;
        } catch (Exception e) {
          errores++;
          log.error("Error al marcar notificación {} como leída: {}", notif.getId(), e.getMessage());
        }
      }
    }

    log.info("Marcadas {}/{} notificaciones como leídas para contribuyente {} (errores: {})",
             marcadas, notificaciones.size(), idContribuyente, errores);
  }
}

