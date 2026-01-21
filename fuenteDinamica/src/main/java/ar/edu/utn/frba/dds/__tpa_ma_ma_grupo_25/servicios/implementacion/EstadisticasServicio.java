package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteDinamicaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeModificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudDeCreacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ISolicitudDeModificacionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEstadisticasServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class EstadisticasServicio implements IEstadisticasServicio {

    private final ISolicitudDeCreacionRepositorioBD solicitudCreacionRepositorio;
    private final ISolicitudDeModificacionRepositorioBD solicitudModificacionRepositorio;

    public EstadisticasServicio(
            ISolicitudDeCreacionRepositorioBD solicitudCreacionRepositorio,
            ISolicitudDeModificacionRepositorioBD solicitudModificacionRepositorio
    ) {
        this.solicitudCreacionRepositorio = solicitudCreacionRepositorio;
        this.solicitudModificacionRepositorio = solicitudModificacionRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasFuenteDinamicaDTO obtenerEstadisticas() {
        log.info("<ESTADISTICAS> Solicitando estadísticas de FuenteDinamica");

        try {
            // Estadísticas de solicitudes de creación
            List<SolicitudDeCreacion> solicitudesCreacion = solicitudCreacionRepositorio.findAll();
            long solCreacionTotal = solicitudesCreacion.size();
            long solCreacionAceptadas = solicitudesCreacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.ACEPTADA)
                    .count();
            long solCreacionRechazadas = solicitudesCreacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.RECHAZADA)
                    .count();
            long solCreacionPendientes = solicitudesCreacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE)
                    .count();
            long solCreacionPendientesMod = solicitudesCreacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION)
                    .count();

            // Estadísticas de solicitudes de modificación
            List<SolicitudDeModificacion> solicitudesModificacion = solicitudModificacionRepositorio.findAll();
            long solModificacionTotal = solicitudesModificacion.size();
            long solModificacionAceptadas = solicitudesModificacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.ACEPTADA)
                    .count();
            long solModificacionRechazadas = solicitudesModificacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.RECHAZADA)
                    .count();
            long solModificacionPendientes = solicitudesModificacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE)
                    .count();
            long solModificacionPendientesMod = solicitudesModificacion.stream()
                    .filter(s -> s.estadoActual().getTipoEstado() == TipoEstadoRevision.PENDIENTE_DE_MODIFICACION)
                    .count();

            EstadisticasFuenteDinamicaDTO estadisticas = new EstadisticasFuenteDinamicaDTO(
                    solCreacionAceptadas, // hechosCreados
                    solModificacionAceptadas, // hechosModificados
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

            log.info("<ESTADISTICAS> FuenteDinamica - Hechos C:{}/M:{}, Sol.Creación: {}/{}/{}/{}, Sol.Modificación: {}/{}/{}/{}",
                    solCreacionAceptadas, solModificacionAceptadas,
                    solCreacionTotal, solCreacionAceptadas, solCreacionRechazadas, solCreacionPendientes,
                    solModificacionTotal, solModificacionAceptadas, solModificacionRechazadas, solModificacionPendientes);

            return estadisticas;

        } catch (Exception e) {
            log.error("<ERROR> Error al obtener estadísticas de FuenteDinamica: {}", e.getMessage());
            return new EstadisticasFuenteDinamicaDTO(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }
}

