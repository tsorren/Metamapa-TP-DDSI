package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeocodingService {

  private final WebClient webClient;

  public GeocodingService(WebClient.Builder builder) {
    this.webClient = builder.baseUrl("https://nominatim.openstreetmap.org").build();
  }

  public String obtenerProvincia(double lat, double lon) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/reverse")
                    .queryParam("format", "json")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .build())
            .retrieve()
            .bodyToMono(JsonNode.class)
            .map(json -> json.path("address").path("state").asText())
            .block();
  }
}

