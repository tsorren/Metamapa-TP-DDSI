package ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.FuenteDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.internal.WebApiCallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FuenteEstaticaClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;
  private final WebApiCallerService webApiCallerService;
  private final WebClient webClient;

  public FuenteEstaticaClient(RestTemplate restTemplate,
                              @Value("${fuenteestatica.url}") String baseUrl,
                              WebApiCallerService webApiCallerService) {
    this.restTemplate = restTemplate;

    this.webApiCallerService = webApiCallerService;

    String urlFinal = baseUrl;
    if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
      urlFinal = "https://" + baseUrl;
      log.info("⚙️ Agregando protocolo HTTPS a la URL del fuente dinamica");
    }

    log.info("🔗 Configurando FuenteDinamicaClient con URL: {}", urlFinal);

    // Extraer host y puerto de la URL
    try {
      String[] urlParts = urlFinal.replace("https://", "").replace("http://", "").split("/")[0].split(":");
      String host = urlParts[0];
      String port = urlParts.length > 1 ? urlParts[1] : (urlFinal.startsWith("https://") ? "443" : "80");
      log.info("📍 Host: {}, Puerto: {}", host, port);
    } catch (Exception e) {
      log.warn("⚠️ No se pudo parsear la URL para mostrar host/puerto: {}", e.getMessage());
    }

    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024)) // 32 MB
        .build();
    this.webClient = WebClient.builder()
        .baseUrl(urlFinal)
        .exchangeStrategies(strategies)
        .build();

    this.baseUrl = urlFinal;
  }

  /**
   * Crea una nueva fuente estática a partir del path del archivo
   */
  public Long crearFuente(String path) {
    Map<String, String> body = Map.of("path", path);
    String url = baseUrl + "/fuentes";
    /*ResponseEntity<Map> response = restTemplate.postForEntity(
            baseUrl + "/fuentes",
            body,
            Map.class
    );*/
    Map response = webApiCallerService.post(url, body, Map.class);

    if (response == null || !response.containsKey("identificador")) {
      throw new IllegalStateException("Respuesta inválida al crear la fuente");
    }

    return ((Number) response.get("identificador")).longValue();
  }

  /**
   * Obtiene todas las fuentes estáticas
   */
  public List<FuenteDTO> obtenerFuentes(LocalDateTime fechaAnteriorA) {
    String url = baseUrl + "/fuentes";
    if (fechaAnteriorA != null) {
      url += "?fechaAnteriorA=" + fechaAnteriorA;
    }

    /*ResponseEntity<FuenteDTO[]> response = restTemplate.getForEntity(url, FuenteDTO[].class);
    return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();*/
    return webApiCallerService.getList(url, FuenteDTO.class);
  }

  /**
   * Importa los hechos asociados a una fuente
   */
  public List<HechoDTO> importarHechos(Long fuenteId) {
    String url = baseUrl + "/fuentes/" + fuenteId;

    /*ResponseEntity<HechoDTO[]> response = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            HechoDTO[].class
    );

    HechoDTO[] hechos = response.getBody();
    return hechos != null ? Arrays.asList(hechos) : List.of();*/

    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient
          .patch()
          .uri(url)
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToFlux(HechoDTO.class)
          .collectList()
          .block();
    });
  }

  /**
   * Obtiene los hechos de una fuente específica
   */
  public List<HechoDTO> obtenerHechosDeFuente(Long fuenteId, LocalDateTime fechaAnteriorA) {
    String url = baseUrl + "/fuentes/" + fuenteId;
    if (fechaAnteriorA != null) {
      url += "?fechaAnteriorA=" + fechaAnteriorA;
    }

    /*ResponseEntity<HechoDTO[]> response = restTemplate.getForEntity(url, HechoDTO[].class);
    return response.getBody() != null ? Arrays.asList(response.getBody()) : List.of();*/
    return webApiCallerService.getList(url, HechoDTO.class);
  }

  /**
   * Oculta un hecho específico
   */
  public void ocultarHecho(Long hechoId) {
    String url = baseUrl + "/fuentes/" + hechoId;
    /*restTemplate.patchForObject(baseUrl + "/ocultar/" + hechoId, null, Void.class);*/
    webApiCallerService.patch(url, null, Void.class);
  }

  /**
   * Sube un archivo CSV directamente al microservicio FuenteEstatica
   * Cambiar la URL para usar el endpoint correcto
   */
  public ResponseEntity<Map> subirYProcesarCSV(MultipartFile archivo) throws IOException {
    String url = baseUrl + "/fuentes/upload";

    ByteArrayResource recurso = new ByteArrayResource(archivo.getBytes()) {
      @Override
      public String getFilename() {
        return archivo.getOriginalFilename();
      }
    };

    /*HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", recurso);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);*/
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("file", recurso);

    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient
          .post()
          .uri(url)
          .header("Authorization", "Bearer " + accessToken)
          .contentType(MediaType.MULTIPART_FORM_DATA)
          .body(BodyInserters.fromMultipartData(builder.build()))
          .retrieve()
          .toEntity(Map.class)
          .block();
    });
  }

  /**
   * Obtener estadísticas de FuenteEstatica
   */
  public Map<String, Object> obtenerEstadisticas() {
    try {
      String url = baseUrl + "/fuentes/admin/estadisticas";
      /*ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/fuentes/admin/estadisticas", Map.class);
      return response.getBody();*/
      return webApiCallerService.get(url, Map.class);
    } catch (Exception e) {
      log.error("Error al obtener estadísticas de FuenteEstatica: {}", e.getMessage());
      return Map.of();
    }
  }

  /**
   * Verificar estado de salud del microservicio
   */
  public boolean verificarEstado() {
    try {
      ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/actuator/health", String.class);
      return response.getStatusCode().is2xxSuccessful();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Obtener el progreso de la importación de una fuente
   */
  public Map<String, Object> obtenerProgresoImportacion(Long fuenteId) {
    try {
      String url = baseUrl + "/fuentes/progreso/" + fuenteId;
      ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
      return response.getBody();
    } catch (Exception e) {
      return null;
    }
  }
}
