package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.SolicitudDeEliminacionOutputDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.SolicitudEliminacionInputDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VisualizarHechosService {

    private final AgregadorClient agregadorClient;

    public VisualizarHechosService(AgregadorClient agregadorClient) {
        this.agregadorClient = agregadorClient;
    }

    /**
     * Solicitar eliminación de un hecho desde visualizarHechos.
     * IMPORTANTE: El idHecho que recibimos YA ES el ID interno de Agregador,
     * por lo tanto NO necesitamos mapearlo.
     */
    public SolicitudDeEliminacionOutputDTO solicitarEliminacion(SolicitudEliminacionInputDTO solicitud) {
        try {
            // Validar justificación mínima de 500 caracteres
            if (solicitud.getJustificacion() == null || solicitud.getJustificacion().trim().length() < 500) {
                throw new IllegalArgumentException("La justificación debe tener al menos 500 caracteres");
            }
            
            Long idHecho = solicitud.getIdHecho();
            log.info("Solicitando eliminación del hecho con ID (Agregador): {}", idHecho);

            // Crear solicitud directamente con el ID recibido (ya es ID interno de Agregador)
            SolicitudEliminacionInputDTO solicitudParaAgregador = new SolicitudEliminacionInputDTO();
            solicitudParaAgregador.setIdHecho(idHecho);
            solicitudParaAgregador.setJustificacion(solicitud.getJustificacion());
            solicitudParaAgregador.setIdContribuyente(solicitud.getIdContribuyente());

            return agregadorClient.solicitarEliminacion(solicitudParaAgregador);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al solicitar eliminación del hecho {}: {}", solicitud.getIdHecho(), e.getMessage());
            throw new RuntimeException("Error al solicitar eliminación del hecho", e);
        }
    }
}
