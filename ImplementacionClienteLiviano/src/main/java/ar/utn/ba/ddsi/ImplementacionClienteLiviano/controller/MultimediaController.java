package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.MultimediaDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controlador que actúa como proxy para operaciones de multimedia.
 * Todas las peticiones se reenvían al servidor de FuenteDinámica.
 */
@Slf4j
@RestController
@RequestMapping("/multimedia")
public class MultimediaController {

  private final FuenteDinamicaClient fuenteDinamicaClient;
  private final RestTemplate restTemplate;

  @Value("${fuentedinamica.url}")
  private String fuenteDinamicaUrl;

  private String fuenteDinamicaUrlNormalizada;

  // Mapeo de extensiones a Content-Types
  private static final Map<String, MediaType> CONTENT_TYPE_MAP = Map.ofEntries(
      // Imágenes
      Map.entry("jpg", MediaType.IMAGE_JPEG),
      Map.entry("jpeg", MediaType.IMAGE_JPEG),
      Map.entry("png", MediaType.IMAGE_PNG),
      Map.entry("gif", MediaType.IMAGE_GIF),
      Map.entry("webp", MediaType.parseMediaType("image/webp")),
      Map.entry("svg", MediaType.parseMediaType("image/svg+xml")),
      Map.entry("bmp", MediaType.parseMediaType("image/bmp")),
      // Videos
      Map.entry("mp4", MediaType.parseMediaType("video/mp4")),
      Map.entry("webm", MediaType.parseMediaType("video/webm")),
      Map.entry("ogv", MediaType.parseMediaType("video/ogg")),
      Map.entry("avi", MediaType.parseMediaType("video/x-msvideo")),
      Map.entry("mov", MediaType.parseMediaType("video/quicktime")),
      // Audio
      Map.entry("mp3", MediaType.parseMediaType("audio/mpeg")),
      Map.entry("wav", MediaType.parseMediaType("audio/wav")),
      Map.entry("ogg", MediaType.parseMediaType("audio/ogg")),
      Map.entry("m4a", MediaType.parseMediaType("audio/mp4")),
      // Texto
      Map.entry("txt", MediaType.TEXT_PLAIN),
      Map.entry("text", MediaType.TEXT_PLAIN)
  );

  public MultimediaController(FuenteDinamicaClient fuenteDinamicaClient, RestTemplate restTemplate) {
    this.fuenteDinamicaClient = fuenteDinamicaClient;
    this.restTemplate = restTemplate;
  }

  @PostConstruct
  public void init() {
    // Normalizar URL agregando protocolo si no lo tiene
    if (fuenteDinamicaUrl != null && !fuenteDinamicaUrl.startsWith("http://") && !fuenteDinamicaUrl.startsWith("https://")) {
      this.fuenteDinamicaUrlNormalizada = "https://" + fuenteDinamicaUrl;
      log.info("📷 MultimediaController - URL normalizada: {}", fuenteDinamicaUrlNormalizada);
    } else {
      this.fuenteDinamicaUrlNormalizada = fuenteDinamicaUrl;
      log.info("📷 MultimediaController - URL ya tiene protocolo: {}", fuenteDinamicaUrlNormalizada);
    }
  }

  /**
   * Obtiene el MediaType adecuado basado en la extensión del archivo
   */
  private MediaType getMediaTypeForFile(String filename) {
    if (filename == null || !filename.contains(".")) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
    String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    return CONTENT_TYPE_MAP.getOrDefault(extension, MediaType.APPLICATION_OCTET_STREAM);
  }

  /**
   * Endpoint para subir archivos multimedia.
   * Actúa como proxy hacia FuenteDinámica.
   */
  @PostMapping(value = "/upload", consumes = "multipart/form-data")
  public ResponseEntity<MultimediaDTO> subirArchivo(@RequestParam("archivo") MultipartFile archivo) {
    try {
      log.info("Recibiendo archivo: {} - Tipo: {} - Tamaño: {} bytes",
               archivo.getOriginalFilename(),
               archivo.getContentType(),
               archivo.getSize());

      // Enviar al servidor de FuenteDinámica
      MultimediaDTO multimedia = fuenteDinamicaClient.subirArchivo(archivo);
      log.info("Archivo subido exitosamente a FuenteDinámica con ID: {}", multimedia.getId());

      return ResponseEntity.ok(multimedia);
    } catch (IllegalArgumentException e) {
      log.error("Error de validación: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      log.error("Error al subir archivo a FuenteDinámica", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint para guardar texto como multimedia.
   * Actúa como proxy hacia FuenteDinámica.
   */
  @PostMapping(value = "/texto", consumes = "text/plain")
  public ResponseEntity<MultimediaDTO> guardarTexto(@RequestBody String texto) {
    try {
      log.info("Recibiendo texto de {} caracteres", texto.length());

      // Enviar al servidor de FuenteDinámica
      MultimediaDTO multimedia = fuenteDinamicaClient.guardarTexto(texto);
      log.info("Texto guardado exitosamente en FuenteDinámica con ID: {}", multimedia.getId());

      return ResponseEntity.ok(multimedia);
    } catch (IllegalArgumentException e) {
      log.error("Error de validación: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      log.error("Error al guardar texto en FuenteDinámica", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint para obtener información de multimedia por ID.
   * Actúa como proxy hacia FuenteDinámica.
   */
  @GetMapping("/{id}")
  public ResponseEntity<MultimediaDTO> obtenerMultimedia(@PathVariable Long id) {
    try {
      MultimediaDTO multimedia = fuenteDinamicaClient.obtenerMultimedia(id);
      return ResponseEntity.ok(multimedia);
    } catch (Exception e) {
      log.error("Error al obtener multimedia con ID {} desde FuenteDinámica", id, e);
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint proxy para servir archivos multimedia desde FuenteDinámica
   */
  @GetMapping("/archivos/{nombreArchivo}")
  public ResponseEntity<Resource> obtenerArchivo(@PathVariable String nombreArchivo) {
    try {
      log.info("Solicitando archivo multimedia: {}", nombreArchivo);

      String url = fuenteDinamicaUrlNormalizada + "/multimedia/archivos/" + nombreArchivo;
      log.info("URL de FuenteDinámica: {}", url);

      ResponseEntity<Resource> response = restTemplate.getForEntity(url, Resource.class);

      if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        // Determinar el Content-Type basado en la extensión del archivo
        MediaType mediaType = getMediaTypeForFile(nombreArchivo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"");
        // Agregar headers de CORS para permitir carga desde cualquier origen
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaders.CACHE_CONTROL, "public, max-age=3600");

        log.info("Archivo {} servido exitosamente con Content-Type: {}", nombreArchivo, mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getBody());
      } else {
        log.warn("FuenteDinámica respondió con status: {} para archivo: {}",
                 response.getStatusCode(), nombreArchivo);
        return ResponseEntity.status(response.getStatusCode()).build();
      }

    } catch (Exception e) {
      log.error("Error al obtener archivo multimedia {}: {}", nombreArchivo, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}

