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
public class SolicitudesService {

  private final AgregadorClient agregadorClient;
  private final FuenteDinamicaClient fuenteDinamicaClient;

  public SolicitudesService(AgregadorClient agregadorClient, FuenteDinamicaClient fuenteDinamicaClient) {
    this.agregadorClient = agregadorClient;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
  }

  /**
   * Obtener todas las solicitudes pendientes de todas las fuentes
   */
  public Map<String, Object> obtenerTodasLasSolicitudesPendientes() {
    Map<String, Object> solicitudes = new HashMap<>();

    try {
      // Solicitudes de eliminación (solo PENDIENTE)
      List<SolicitudDeEliminacionOutputDTO> eliminacionPendiente = 
              agregadorClient.obtenerSolicitudesEliminacionPorEstado(EstadoAtencionSolicitud.PENDIENTE);
      solicitudes.put("eliminacion", Map.of(
              "pendientes", eliminacionPendiente != null ? eliminacionPendiente : new ArrayList<>()
      ));
    } catch (Exception e) {
      log.error("Error al obtener solicitudes de eliminación: {}", e.getMessage());
      solicitudes.put("eliminacion", Map.of("pendientes", new ArrayList<>()));
    }

    try {
      // Solicitudes de creación (PENDIENTE y PENDIENTE_DE_MODIFICACION)
      List<SolicitudCreacionOutputDTO> creacionPendiente =
              fuenteDinamicaClient.obtenerSolicitudesCreacionPorEstado(TipoEstadoRevision.PENDIENTE);
      List<SolicitudCreacionOutputDTO> creacionPendienteModificacion =
             fuenteDinamicaClient.obtenerSolicitudesCreacionPorEstado(TipoEstadoRevision.PENDIENTE_DE_MODIFICACION);
      
      solicitudes.put("creacion", Map.of(
              "pendientes", creacionPendiente != null ? creacionPendiente : new ArrayList<>(),
              "pendientesModificacion", creacionPendienteModificacion != null ? creacionPendienteModificacion : new ArrayList<>()
      ));
    } catch (Exception e) {
      log.error("Error al obtener solicitudes de creación: {}", e.getMessage());
      solicitudes.put("creacion", Map.of(
              "pendientes", new ArrayList<>(),
              "pendientesModificacion", new ArrayList<>()
      ));
    }

    try {
      // Solicitudes de modificación (PENDIENTE y PENDIENTE_DE_MODIFICACION)
      // IMPORTANTE: Usar SolicitudDeModificacionOutputDTO que incluye el campo "modificacion"
      List<SolicitudDeModificacionOutputDTO> modificacionPendiente = 
              fuenteDinamicaClient.obtenerSolicitudesModificacionPorEstadoConModificacion(TipoEstadoRevision.PENDIENTE);
      List<SolicitudDeModificacionOutputDTO> modificacionPendienteModificacion = 
              fuenteDinamicaClient.obtenerSolicitudesModificacionPorEstadoConModificacion(TipoEstadoRevision.PENDIENTE_DE_MODIFICACION);
      
      solicitudes.put("modificacion", Map.of(
              "pendientes", modificacionPendiente != null ? modificacionPendiente : new ArrayList<>(),
              "pendientesModificacion", modificacionPendienteModificacion != null ? modificacionPendienteModificacion : new ArrayList<>()
      ));
    } catch (Exception e) {
      log.error("Error al obtener solicitudes de modificación: {}", e.getMessage());
      solicitudes.put("modificacion", Map.of(
              "pendientes", new ArrayList<>(),
              "pendientesModificacion", new ArrayList<>()
      ));
    }

    return solicitudes;
  }

  /**
   * Procesar una solicitud de eliminación
   */
  public SolicitudDeEliminacionOutputDTO procesarSolicitudEliminacion(Long idSolicitud, ProcesamientoSolicitudEliminacionInputDTO procesamiento) {
    try {
      return agregadorClient.procesarSolicitudEliminacion(idSolicitud, procesamiento);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de eliminación {}: {}", idSolicitud, e.getMessage());
      throw new RuntimeException("Error al procesar la solicitud de eliminación", e);
    }
  }

  /**
   * Procesar una solicitud de creación
   */
  public SolicitudCreacionOutputDTO procesarSolicitudCreacion(Long idSolicitud, ProcesamientoSolicitudRevisionInputDTO procesamiento) {
    try {
      return fuenteDinamicaClient.procesarSolicitudCreacion(idSolicitud, procesamiento);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de creación {}: {}", idSolicitud, e.getMessage());
      throw new RuntimeException("Error al procesar la solicitud de creación", e);
    }
  }

  /**
   * Procesar una solicitud de modificación
   */
  public SolicitudDeModificacionOutputDTO procesarSolicitudModificacion(Long idSolicitud, ProcesamientoSolicitudRevisionInputDTO procesamiento) {
    try {
      return fuenteDinamicaClient.procesarSolicitudModificacion(idSolicitud, procesamiento);
    } catch (Exception e) {
      log.error("Error al procesar solicitud de modificación {}: {}", idSolicitud, e.getMessage());
      throw new RuntimeException("Error al procesar la solicitud de modificación", e);
    }
  }
}
