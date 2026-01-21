package ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.internal.WebApiCallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FuenteDinamicaClient {
  private final String fuenteDinamicaUrl;
  private final WebClient webClient;
  private final RestTemplate restTemplate;
  private final WebApiCallerService webApiCallerService;

  public FuenteDinamicaClient(@Value("${fuentedinamica.url}") String fuenteDinamicaUrl, RestTemplate restTemplate, WebApiCallerService webApiCallerService) {
    //this.webClient = WebClient.builder().baseUrl(fuenteDinamicaUrl).build();
    //this.fuenteDinamicaUrl = fuenteDinamicaUrl;
    this.restTemplate = restTemplate;
    this.webApiCallerService = webApiCallerService;

    String urlFinal = fuenteDinamicaUrl;
    if (!fuenteDinamicaUrl.startsWith("http://") && !fuenteDinamicaUrl.startsWith("https://")) {
      urlFinal = "https://" + fuenteDinamicaUrl;
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
    this.fuenteDinamicaUrl = urlFinal;
  }
  public ContribuyenteDinamicaOutputDTO registrarContribuyente(ContribuyenteDinamicaOutputDTO contribuyente) {
    // El registro de contribuyente no requiere autenticación (usuario no logueado)
    return webApiCallerService.postPublic(fuenteDinamicaUrl + "/contribuyentes", contribuyente, ContribuyenteDinamicaOutputDTO.class);
  }

  public AdministradorDinamicaOutputDTO registrarAdministrador(AdministradorDinamicaOutputDTO admin) {
    // El registro de administrador requiere autenticación (solo un admin puede crear otro admin)
    return webApiCallerService.post(fuenteDinamicaUrl + "/admin", admin, AdministradorDinamicaOutputDTO.class);
  }

  public HechoDTO modificarHecho(HechoModificacionDTO hecho) {
    /*return webClient.patch()
            .uri("/fuentes/{idHecho}", hecho.getIdHecho())
            .bodyValue(hecho)
            .retrieve()
            .bodyToMono(HechoDTO.class)
            .block();*/
    return webApiCallerService.patch(fuenteDinamicaUrl+"/hecho/"+hecho.getIdHecho(), hecho, HechoDTO.class);
  }

  public HechoDTO crearHecho(HechoInputDTO hecho) {
    /*return webClient.post()
            .uri("/fuentes")
            .bodyValue(hecho)
            .retrieve()
            .bodyToMono(HechoDTO.class)
            .block();*/
    return webApiCallerService.post(fuenteDinamicaUrl+"/fuentes", hecho, HechoDTO.class);
  }

  /**
   * Obtener solicitudes de creación por estado
   */
  public List<SolicitudCreacionOutputDTO> obtenerSolicitudesCreacionPorEstado(TipoEstadoRevision estado) {
    return webApiCallerService.executeWithTokenRetry( accessToken -> {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/solicitudes-creacion")
              .queryParam("estado", estado.name())
              .build())
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToFlux(SolicitudCreacionOutputDTO.class)
          .collectList()
          .block();
    });
  }

  /**
   * Obtener solicitudes de modificación por estado
   */
  public List<SolicitudCreacionOutputDTO> obtenerSolicitudesModificacionPorEstado(TipoEstadoRevision estado) {
    return webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/solicitudes-modificacion")
                    .queryParam("estado", estado.name())
                    .build())
            .retrieve()
            .bodyToFlux(SolicitudCreacionOutputDTO.class)
            .collectList()
            .block();
  }

  /**
   * Obtener solicitudes de modificación por estado CON el campo modificacion.
   * Este método usa SolicitudDeModificacionOutputDTO que incluye el objeto modificacion.
   */
  public List<SolicitudDeModificacionOutputDTO> obtenerSolicitudesModificacionPorEstadoConModificacion(TipoEstadoRevision estado) {
    return webApiCallerService.executeWithTokenRetry( accessToken -> {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/solicitudes-modificacion")
              .queryParam("estado", estado.name())
              .build())
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToFlux(SolicitudDeModificacionOutputDTO.class)
          .collectList()
          .block();
    });
  }

  /**
   * Procesar solicitud de creación
   */
  public SolicitudCreacionOutputDTO procesarSolicitudCreacion(Long idSolicitud, ProcesamientoSolicitudRevisionInputDTO procesamiento) {
    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.patch()
          .uri("/solicitudes-creacion/{idSolicitud}", idSolicitud)
          .header("Authorization", "Bearer " + accessToken)
          .bodyValue(procesamiento)
          .retrieve()
          .bodyToMono(SolicitudCreacionOutputDTO.class)
          .block();
    });
  }

  /**
   * Procesar solicitud de modificación
   */
  public SolicitudDeModificacionOutputDTO procesarSolicitudModificacion(Long idSolicitud, ProcesamientoSolicitudRevisionInputDTO procesamiento) {
    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.patch()
          .uri("/solicitudes-modificacion/{idSolicitud}", idSolicitud)
          .header("Authorization", "Bearer " + accessToken)
          .bodyValue(procesamiento)
          .retrieve()
          .bodyToMono(SolicitudDeModificacionOutputDTO.class)
          .block();
    });
  }

  /**
   * Obtener hechos de un contribuyente
   */
  public List<HechoOutputDTO> obtenerHechosDeContribuyente(Long idContribuyente) {
    /*return webClient.get()
            .uri("/contribuyentes/{id}/hechos", idContribuyente)
            .retrieve()
            .bodyToFlux(HechoOutputDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(fuenteDinamicaUrl + "/contribuyentes/"+idContribuyente+"/hechos", HechoOutputDTO.class);
  }

  /**
   * Modificar hecho
   */
  public HechoOutputDTO modificarHechoContribuyente(HechoModificacionDTO input) {
    /*return webClient.patch()
            .uri("/fuentes/{idHecho}", input.getIdHecho())
            .bodyValue(input)
            .retrieve()
            .bodyToMono(HechoOutputDTO.class)
            .block();*/
    return webApiCallerService.patch(fuenteDinamicaUrl+"/fuentes/"+input.getIdHecho(), input, HechoOutputDTO.class);
  }

  /**
   * Solicitar modificación de un hecho.
   * POST a /solicitudes-modificacion con solo los campos modificados.
   */
  public SolicitudDeModificacionOutputDTO solicitarModificacionHecho(HechoModificacionDTO input) {
    /*return webClient.post()
            .uri("/solicitudes-modificacion")
            .bodyValue(input)
            .retrieve()
            .bodyToMono(SolicitudDeModificacionOutputDTO.class)
            .block();*/
    return webApiCallerService.post(fuenteDinamicaUrl + "/solicitudes-modificacion", input, SolicitudDeModificacionOutputDTO.class);
  }

  public MultimediaDTO crearMultimedia(TipoContenido tipo, String url) {
    String endpoint = fuenteDinamicaUrl + "/multimedia";

    Map<String, Object> body = new HashMap<>();
    body.put("tipoContenido", tipo.toString());
    body.put("url", url);

    try {
      /*HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<MultimediaDTO> response = restTemplate.postForEntity(endpoint, requestEntity, MultimediaDTO.class);
      return response.getBody();*/

      return webApiCallerService.executeWithTokenRetry(accessToken -> {
        return webClient
            .post()
            .uri(endpoint)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(MultimediaDTO.class)
            .block();
      });
    } catch (Exception e) {
      log.error("Error al crear multimedia: {}", e.getMessage());
      throw new RuntimeException("Error al crear multimedia", e);
    }
  }

  public MultimediaDTO obtenerMultimedia(Long id) {
    String endpoint = fuenteDinamicaUrl + "/multimedia/" + id;

    try {
      /*return restTemplate.getForObject(endpoint, MultimediaDTO.class);*/
      return webApiCallerService.get(endpoint, MultimediaDTO.class);
    } catch (Exception e) {
      log.error("Error al obtener multimedia con ID {}: {}", id, e.getMessage());
      throw new RuntimeException("Error al obtener multimedia", e);
    }
  }

  /**
   * Subir un archivo multimedia a FuenteDinámica
   */
  public MultimediaDTO subirArchivo(org.springframework.web.multipart.MultipartFile archivo) {
    try {
      /*HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("archivo", archivo.getResource());

      // Detectar y agregar tipo de contenido
      TipoContenido tipoContenido = detectarTipoContenido(archivo.getContentType());
      body.add("tipoContenido", tipoContenido.name());

      HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
          new HttpEntity<>(body, headers);

      String endpoint = fuenteDinamicaUrl + "/multimedia/upload";

      ResponseEntity<MultimediaDTO> response =
          restTemplate.postForEntity(endpoint, requestEntity, MultimediaDTO.class);

      log.info("Archivo subido exitosamente: tipo={}, id={}", tipoContenido, response.getBody().getId());
      return response.getBody();*/
      String endpoint = fuenteDinamicaUrl + "/multimedia/upload";

      TipoContenido tipoContenido = detectarTipoContenido(archivo.getContentType());

      MultipartBodyBuilder builder = new MultipartBodyBuilder();
      builder.part("archivo", archivo.getResource());
      builder.part("tipoContenido", tipoContenido.name());

      MultimediaDTO response = webApiCallerService.executeWithTokenRetry(accessToken -> {
        return webClient
            .post()
            .uri(endpoint)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .bodyToMono(MultimediaDTO.class)
            .block();
      });

      log.info("Archivo subido exitosamente: tipo={}, id={}", tipoContenido, response.getId());
      return response;
    } catch (Exception e) {
      log.error("Error al subir archivo a FuenteDinámica: {}", e.getMessage());
      throw new RuntimeException("Error al subir archivo", e);
    }
  }

  /**
   * Detectar tipo de contenido desde el MIME type
   */
  private TipoContenido detectarTipoContenido(String contentType) {
    if (contentType == null) {
      return TipoContenido.TEXTO;
    }

    String tipo = contentType.toLowerCase();
    if (tipo.startsWith("image/")) {
      return TipoContenido.IMAGEN;
    } else if (tipo.startsWith("video/")) {
      return TipoContenido.VIDEO;
    } else if (tipo.startsWith("audio/")) {
      return TipoContenido.AUDIO;
    } else if (tipo.contains("text/")) {
      return TipoContenido.TEXTO;
    }

    return TipoContenido.TEXTO; // Default
  }

  /**
   * Guardar texto como multimedia en FuenteDinámica
   */
  public MultimediaDTO guardarTexto(String texto) {
    try {
      /*HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.TEXT_PLAIN);

      HttpEntity<String> requestEntity = new HttpEntity<>(texto, headers);



      ResponseEntity<MultimediaDTO> response =
          restTemplate.postForEntity(endpoint, requestEntity, MultimediaDTO.class);

      return response.getBody();*/
      String endpoint = fuenteDinamicaUrl + "/multimedia/texto";

      return webApiCallerService.executeWithTokenRetry(accessToken -> {
        return webClient
            .post()
            .uri(endpoint)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(texto)
            .retrieve()
            .bodyToMono(MultimediaDTO.class)
            .block();
      });
    } catch (Exception e) {
      log.error("Error al guardar texto en FuenteDinámica: {}", e.getMessage());
      throw new RuntimeException("Error al guardar texto", e);
    }
  }

  /**
   * Obtener estadísticas de FuenteDinamica
   */
  public Map<String, Object> obtenerEstadisticas() {
    try {
      String url = "/admin/estadisticas";
      /*return webApiCallerService.executeWithTokenRetry(accessToken -> {
        return webClient.get()
            .uri("/admin/estadisticas")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
            .block();
      });*/
      return webApiCallerService.getForStats(url, new ParameterizedTypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      log.error("Error al obtener estadísticas de FuenteDinámica: {}", e.getMessage());
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

  // ==================== NOTIFICACIONES ====================

  public List<NotificacionDTO> obtenerNotificacionesDeAdministrador(Long idAdministrador) {
    /*return webClient.get()
            .uri("/notificaciones/administrador/{id}", idAdministrador)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(fuenteDinamicaUrl+"/notificaciones/administrador/"+idAdministrador, NotificacionDTO.class);
  }

  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeAdministrador(Long idAdministrador) {
    /*return webClient.get()
            .uri("/notificaciones/administrador/{id}/no-leidas", idAdministrador)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(fuenteDinamicaUrl+"/notificaciones/administrador/"+idAdministrador+"/no-leidas", NotificacionDTO.class);

  }

  public List<NotificacionDTO> obtenerNotificacionesDeContribuyente(Long idContribuyente) {
    /*return webClient.get()
            .uri("/notificaciones/contribuyente/{id}", idContribuyente)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(fuenteDinamicaUrl + "/notificaciones/contribuyente/" + idContribuyente, NotificacionDTO.class);
  }

  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeContribuyente(Long idContribuyente) {
    /*return webClient.get()
            .uri("/notificaciones/contribuyente/{id}/no-leidas", idContribuyente)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(fuenteDinamicaUrl + "/notificaciones/contribuyente/" + idContribuyente + "/no-leidas", NotificacionDTO.class);

  }

  public void marcarNotificacionComoLeida(Long idNotificacion) {
    webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.patch()
          .uri("/notificaciones/{id}/marcar-leida", idNotificacion)
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(Void.class)
          .block();
    });
  }
}
