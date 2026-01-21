package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.MultimediaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoContenido;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IMultimediaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/multimedia")
public class MultimediaControlador {

  private final IMultimediaServicio multimediaServicio;

  @Value("${upload.path:uploads}")
  private String uploadPath;

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

  public MultimediaControlador(IMultimediaServicio multimediaServicio) {
    this.multimediaServicio = multimediaServicio;
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

  @PostMapping
  public MultimediaOutputDTO crear(@RequestBody MultimediaInputDTO input) {
    return multimediaServicio.crear(input.getTipoContenido(), input.getUrl());
  }

  @PostMapping(value = "/texto", consumes = MediaType.TEXT_PLAIN_VALUE)
  public MultimediaOutputDTO crearTexto(@RequestBody String texto) {
    return multimediaServicio.crear(TipoContenido.TEXTO, texto);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public MultimediaOutputDTO subirArchivo(
      @RequestParam("archivo") MultipartFile archivo,
      @RequestParam("tipoContenido") TipoContenido tipoContenido) throws IOException {
    return multimediaServicio.subirArchivo(archivo, tipoContenido);
  }

  @GetMapping("/{id}")
  public MultimediaOutputDTO obtenerMultimedia(@PathVariable Long id) {
    return multimediaServicio.encontrarPorId(id);
  }

  @GetMapping("/archivos/{nombreArchivo}")
  public ResponseEntity<Resource> descargarArchivo(@PathVariable String nombreArchivo) throws IOException {
    log.info("📥 Solicitud de archivo: {}", nombreArchivo);

    Path rutaArchivo = Paths.get(uploadPath).resolve(nombreArchivo).normalize();
    log.info("📁 Ruta completa: {}", rutaArchivo.toAbsolutePath());

    Resource recurso = new UrlResource(rutaArchivo.toUri());

    if (recurso.exists() && recurso.isReadable()) {
      // Determinar el Content-Type basado en la extensión
      MediaType mediaType = getMediaTypeForFile(nombreArchivo);

      log.info("✅ Archivo encontrado: {} - Content-Type: {}", nombreArchivo, mediaType);

      return ResponseEntity.ok()
          .contentType(mediaType)
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"")
          .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
          .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
          .body(recurso);
    } else {
      log.warn("❌ Archivo no encontrado o no legible: {} (existe: {}, legible: {})",
               rutaArchivo.toAbsolutePath(), recurso.exists(), recurso.isReadable());
      return ResponseEntity.notFound().build();
    }
  }
}
