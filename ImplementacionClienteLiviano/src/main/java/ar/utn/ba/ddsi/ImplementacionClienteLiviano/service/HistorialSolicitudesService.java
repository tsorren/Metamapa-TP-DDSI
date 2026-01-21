package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HistorialSolicitudesService {

  private final AgregadorClient agregadorClient;
  private final FuenteDinamicaClient fuenteDinamicaClient;

  public HistorialSolicitudesService(AgregadorClient agregadorClient, FuenteDinamicaClient fuenteDinamicaClient) {
    this.agregadorClient = agregadorClient;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
  }

  /**
   * Obtener el historial completo de todas las solicitudes (todos los estados)
   */
  public Map<String, Object> obtenerHistorialCompleto() {
    Map<String, Object> historial = new HashMap<>();

    // Solicitudes de Eliminación (todos los estados)
    try {
      Map<String, List<SolicitudDeEliminacionOutputDTO>> eliminacion = new HashMap<>();
      for (EstadoAtencionSolicitud estado : EstadoAtencionSolicitud.values()) {
        try {
          List<SolicitudDeEliminacionOutputDTO> solicitudes =
                  agregadorClient.obtenerSolicitudesEliminacionPorEstado(estado);
          eliminacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
        } catch (Exception e) {
          log.error("Error al obtener solicitudes de eliminación en estado {}: {}", estado, e.getMessage());
          eliminacion.put(estado.name(), new ArrayList<>());
        }
      }
      historial.put("eliminacion", eliminacion);
    } catch (Exception e) {
      log.error("Error al obtener historial de solicitudes de eliminación: {}", e.getMessage());
      historial.put("eliminacion", new HashMap<>());
    }

    // Solicitudes de Creación (todos los estados)
    try {
      Map<String, List<SolicitudCreacionOutputDTO>> creacion = new HashMap<>();
      for (TipoEstadoRevision estado : TipoEstadoRevision.values()) {
        try {
          List<SolicitudCreacionOutputDTO> solicitudes =
                  fuenteDinamicaClient.obtenerSolicitudesCreacionPorEstado(estado);
          creacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
        } catch (Exception e) {
          log.error("Error al obtener solicitudes de creación en estado {}: {}", estado, e.getMessage());
          creacion.put(estado.name(), new ArrayList<>());
        }
      }
      historial.put("creacion", creacion);
    } catch (Exception e) {
      log.error("Error al obtener historial de solicitudes de creación: {}", e.getMessage());
      historial.put("creacion", new HashMap<>());
    }

    // Solicitudes de Modificación (todos los estados)
    try {
      Map<String, List<SolicitudDeModificacionOutputDTO>> modificacion = new HashMap<>();
      for (TipoEstadoRevision estado : TipoEstadoRevision.values()) {
        try {
          List<SolicitudDeModificacionOutputDTO> solicitudes =
                  fuenteDinamicaClient.obtenerSolicitudesModificacionPorEstadoConModificacion(estado);
          modificacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
        } catch (Exception e) {
          log.error("Error al obtener solicitudes de modificación en estado {}: {}", estado, e.getMessage());
          modificacion.put(estado.name(), new ArrayList<>());
        }
      }
      historial.put("modificacion", modificacion);
    } catch (Exception e) {
      log.error("Error al obtener historial de solicitudes de modificación: {}", e.getMessage());
      historial.put("modificacion", new HashMap<>());
    }

    return historial;
  }

  /**
   * Obtener historial de un tipo específico de solicitud
   */
  public Map<String, Object> obtenerHistorialPorTipo(String tipo) {
    Map<String, Object> historial = new HashMap<>();

    switch (tipo.toLowerCase()) {
      case "eliminacion":
        Map<String, List<SolicitudDeEliminacionOutputDTO>> eliminacion = new HashMap<>();
        for (EstadoAtencionSolicitud estado : EstadoAtencionSolicitud.values()) {
          try {
            List<SolicitudDeEliminacionOutputDTO> solicitudes =
                    agregadorClient.obtenerSolicitudesEliminacionPorEstado(estado);
            eliminacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
          } catch (Exception e) {
            log.error("Error al obtener solicitudes de eliminación en estado {}: {}", estado, e.getMessage());
            eliminacion.put(estado.name(), new ArrayList<>());
          }
        }
        historial.put("solicitudes", eliminacion);
        break;

      case "creacion":
        Map<String, List<SolicitudCreacionOutputDTO>> creacion = new HashMap<>();
        for (TipoEstadoRevision estado : TipoEstadoRevision.values()) {
          try {
            List<SolicitudCreacionOutputDTO> solicitudes =
                    fuenteDinamicaClient.obtenerSolicitudesCreacionPorEstado(estado);
            creacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
          } catch (Exception e) {
            log.error("Error al obtener solicitudes de creación en estado {}: {}", estado, e.getMessage());
            creacion.put(estado.name(), new ArrayList<>());
          }
        }
        historial.put("solicitudes", creacion);
        break;

      case "modificacion":
        Map<String, List<SolicitudDeModificacionOutputDTO>> modificacion = new HashMap<>();
        for (TipoEstadoRevision estado : TipoEstadoRevision.values()) {
          try {
            List<SolicitudDeModificacionOutputDTO> solicitudes =
                    fuenteDinamicaClient.obtenerSolicitudesModificacionPorEstadoConModificacion(estado);
            modificacion.put(estado.name(), solicitudes != null ? solicitudes : new ArrayList<>());
          } catch (Exception e) {
            log.error("Error al obtener solicitudes de modificación en estado {}: {}", estado, e.getMessage());
            modificacion.put(estado.name(), new ArrayList<>());
          }
        }
        historial.put("solicitudes", modificacion);
        break;

      default:
        throw new IllegalArgumentException("Tipo de solicitud no válido: " + tipo);
    }

    return historial;
  }
}

