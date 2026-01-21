package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteEstaticaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteProxyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MicroserviceManagerService {

  private final FuenteEstaticaClient fuenteEstaticaClient;
  private final FuenteDinamicaClient fuenteDinamicaClient;
  private final FuenteProxyClient fuenteProxyClient;
  private final AgregadorClient agregadorClient;

  public MicroserviceManagerService(FuenteEstaticaClient fuenteEstaticaClient,
                                     FuenteDinamicaClient fuenteDinamicaClient,
                                     FuenteProxyClient fuenteProxyClient,
                                     AgregadorClient agregadorClient) {
    this.fuenteEstaticaClient = fuenteEstaticaClient;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
    this.fuenteProxyClient = fuenteProxyClient;
    this.agregadorClient = agregadorClient;
  }

  /**
   * Obtiene el estado de los microservicios consultando sus endpoints de salud
   */
  public MicroserviceStatus obtenerEstadoMicroservicios() {
    MicroserviceStatus status = new MicroserviceStatus();
    status.setFuenteEstaticaRunning(fuenteEstaticaClient.verificarEstado());
    status.setFuenteDinamicaRunning(fuenteDinamicaClient.verificarEstado());
    status.setFuenteProxyRunning(fuenteProxyClient.verificarEstado());
    status.setAgregadorRunning(agregadorClient.verificarEstado());
    return status;
  }

  /**
   * Verifica si un servicio específico está disponible usando su URL
   */
  public boolean verificarEstado(String serviceUrl) {
    try {
      org.springframework.web.reactive.function.client.WebClient webClient =
        org.springframework.web.reactive.function.client.WebClient.builder()
          .baseUrl(serviceUrl)
          .build();

      webClient.get()
        .uri("/actuator/health")
        .retrieve()
        .bodyToMono(String.class)
        .block(java.time.Duration.ofSeconds(5));

      return true;
    } catch (Exception e) {
      log.warn("Servicio no disponible en {}: {}", serviceUrl, e.getMessage());
      return false;
    }
  }


  // Clase interna para el estado - actualizada
  @lombok.Data
  public static class MicroserviceStatus {
    private boolean fuenteEstaticaRunning;
    private boolean fuenteDinamicaRunning;
    private boolean fuenteProxyRunning;
    private boolean agregadorRunning;
  }
}

