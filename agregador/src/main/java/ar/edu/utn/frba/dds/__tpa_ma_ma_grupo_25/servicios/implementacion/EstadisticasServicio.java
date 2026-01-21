package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasAgregadorDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IColeccionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudEliminacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEstadisticasServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EstadisticasServicio implements IEstadisticasServicio {

    private final IColeccionRepositorioBD coleccionRepositorio;
    private final ISolicitudEliminacionRepositorioBD solicitudEliminacionRepositorio;
    private final IFuenteRepositorioBD fuenteRepositorio;

    public EstadisticasServicio(
            IColeccionRepositorioBD coleccionRepositorio,
            ISolicitudEliminacionRepositorioBD solicitudEliminacionRepositorio,
            IFuenteRepositorioBD fuenteRepositorio
    ) {
        this.coleccionRepositorio = coleccionRepositorio;
        this.solicitudEliminacionRepositorio = solicitudEliminacionRepositorio;
        this.fuenteRepositorio = fuenteRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasAgregadorDTO obtenerEstadisticas() {
        // Log comentado para producción
        // log.info("<ESTADISTICAS> Solicitando estadísticas del agregador");

        try {
            // Estadísticas de colecciones
            // log.debug("Obteniendo colecciones...");
            List<Coleccion> colecciones = coleccionRepositorio.findAll();
            // log.debug("Colecciones obtenidas: {}", colecciones.size());

            long coleccionesCreadas = colecciones.size();
            long coleccionesModificadas = 0L;

            try {
                coleccionesModificadas = colecciones.stream()
                        .mapToLong(c -> {
                            Long contador = c.getContadorModificaciones();
                            return contador != null ? contador : 0L;
                        })
                        .sum();
                // log.debug("Colecciones modificadas: {}", coleccionesModificadas);
            } catch (Exception e) {
                log.error("Error al calcular colecciones modificadas: {}", e.getMessage());
                coleccionesModificadas = 0L;
            }

            // Estadísticas de fuentes activas
            // log.debug("Obteniendo fuentes activas...");
            long fuentesActivas = fuenteRepositorio.count();
            // log.debug("Fuentes activas: {}", fuentesActivas);

            // Estadísticas de solicitudes de eliminación
            // log.debug("Obteniendo solicitudes de eliminación...");
            List<SolicitudDeEliminacion> solicitudes = solicitudEliminacionRepositorio.findAll();
            // log.debug("Solicitudes obtenidas: {}", solicitudes.size());

            long solicitudesTotal = solicitudes.size();

            long solicitudesAceptadas = solicitudes.stream()
                    .filter(s -> s.getEstadoAtencionSolicitud() != null &&
                                 s.getEstadoAtencionSolicitud() == EstadoAtencionSolicitud.ACEPTADA)
                    .count();

            long solicitudesRechazadas = solicitudes.stream()
                    .filter(s -> s.getEstadoAtencionSolicitud() != null &&
                                 s.getEstadoAtencionSolicitud() == EstadoAtencionSolicitud.RECHAZADA)
                    .count();

            long solicitudesPendientes = solicitudes.stream()
                    .filter(s -> s.getEstadoAtencionSolicitud() != null &&
                                 s.getEstadoAtencionSolicitud() == EstadoAtencionSolicitud.PENDIENTE)
                    .count();

            // log.debug("Creando DTO con valores: coleccionesCreadas={}, coleccionesModificadas={}, fuentesActivas={}, solicitudesTotal={}",
            //         coleccionesCreadas, coleccionesModificadas, fuentesActivas, solicitudesTotal);

            EstadisticasAgregadorDTO estadisticas = new EstadisticasAgregadorDTO(
                    coleccionesCreadas,
                    coleccionesModificadas,
                    fuentesActivas,
                    solicitudesTotal,
                    solicitudesAceptadas,
                    solicitudesRechazadas,
                    solicitudesPendientes
            );

            // Log comentado para producción
            // log.info("<ESTADISTICAS> Agregador - Colecciones: {}/{}, Fuentes: {}, Solicitudes Elim: {}/{}/{}/{}",
            //         coleccionesCreadas, coleccionesModificadas, fuentesActivas,
            //         solicitudesTotal, solicitudesAceptadas, solicitudesRechazadas, solicitudesPendientes);

            return estadisticas;

        } catch (Exception e) {
            log.error("<ERROR> Error al obtener estadísticas del agregador: {}", e.getMessage(), e);
            return new EstadisticasAgregadorDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }
}

