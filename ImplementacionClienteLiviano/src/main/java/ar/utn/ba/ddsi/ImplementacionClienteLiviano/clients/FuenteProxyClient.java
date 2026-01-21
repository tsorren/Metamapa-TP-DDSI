package ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FuenteProxyClient {

  private final WebClient webClient;

  public FuenteProxyClient(@Value("${fuenteproxy.url}") String fuenteProxyUrl) {
    this.webClient = WebClient.builder()
            .baseUrl(fuenteProxyUrl)
            .build();
  }

  /**
   * Obtener estadísticas de FuenteProxy
   */
  public Map<String, Object> obtenerEstadisticas() {
    try {
      return webClient.get()
              .uri("/admin/estadisticas")
              .retrieve()
              .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
              .block();
    } catch (Exception e) {
      log.error("Error al obtener estadísticas de FuenteProxy: {}", e.getMessage());
      return new HashMap<>();
    }
  }

  /**
   * Verificar estado de salud del microservicio
   */
  public boolean verificarEstado() {
    try {
      webClient.get()
              .uri("/actuator/health")
              .retrieve()
              .bodyToMono(String.class)
              .block();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Crear fuente proxy recursiva (MetaMapa)
   */
  public Map<String, Object> crearFuenteProxyRecursiva(String url, String nombre) {
    try {
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("url", url);
      requestBody.put("nombre", nombre);

      return webClient.post()
              .uri("/recursiva")
              .bodyValue(requestBody)
              .retrieve()
              .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
              .block();
    } catch (Exception e) {
      log.error("Error al crear fuente proxy recursiva: {}", e.getMessage());
      throw new RuntimeException("Error al crear fuente proxy recursiva: " + e.getMessage());
    }
  }

  /**
   * Crear fuente proxy externa (DesastreAPI)
   */
  public Map<String, Object> crearFuenteProxyExterna(String url, String email, String password, String nombre) {
    try {
      Map<String, String> requestBody = new HashMap<>();
      requestBody.put("url", url);
      requestBody.put("email", email);
      requestBody.put("password", password);
      requestBody.put("nombre", nombre);

      return webClient.post()
              .uri("/externa")
              .bodyValue(requestBody)
              .retrieve()
              .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
              .block();
    } catch (Exception e) {
      log.error("Error al crear fuente proxy externa: {}", e.getMessage());
      throw new RuntimeException("Error al crear fuente proxy externa: " + e.getMessage());
    }
  }
}

