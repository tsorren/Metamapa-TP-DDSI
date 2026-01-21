package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.EstadisticasClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteEstaticaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteProxyClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.EstadisticasDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EstadisticasService {

    private final AgregadorClient agregadorClient;
    private final FuenteEstaticaClient fuenteEstaticaClient;
    private final FuenteDinamicaClient fuenteDinamicaClient;
    private final FuenteProxyClient fuenteProxyClient;
    private final EstadisticasClient estadisticasClient;

    public EstadisticasService(AgregadorClient agregadorClient,
                               FuenteEstaticaClient fuenteEstaticaClient,
                               FuenteDinamicaClient fuenteDinamicaClient,
                               FuenteProxyClient fuenteProxyClient,
                               EstadisticasClient estadisticasClient) {
        this.agregadorClient = agregadorClient;
        this.fuenteEstaticaClient = fuenteEstaticaClient;
        this.fuenteDinamicaClient = fuenteDinamicaClient;
        this.fuenteProxyClient = fuenteProxyClient;
        this.estadisticasClient = estadisticasClient;
    }

    /**
     * Obtiene estadísticas consolidadas de todos los microservicios
     */
    public EstadisticasDTO obtenerEstadisticasConsolidadas() {
        log.info("Obteniendo estadísticas consolidadas desde EstadisticasService");

        try {
            // Usar el nuevo método del EstadisticasClient que obtiene todas las estadísticas
            return estadisticasClient.obtenerEstadisticasPanelControl();
        } catch (Exception e) {
            log.error("Error al obtener estadísticas consolidadas: {}", e.getMessage());
            // Retornar estadísticas vacías en caso de error
            return new EstadisticasDTO();
        }
    }
}


