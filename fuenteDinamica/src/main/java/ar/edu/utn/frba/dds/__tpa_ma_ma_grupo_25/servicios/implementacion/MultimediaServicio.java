package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoContenido;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContenidoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IMultimediaServicio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class MultimediaServicio implements IMultimediaServicio {

  private final IContenidoRepositorioBD contenidoRepositorio;

  @Value("${upload.path:uploads}")
  private String uploadPath;

  public MultimediaServicio(IContenidoRepositorioBD contenidoRepositorio) {
    this.contenidoRepositorio = contenidoRepositorio;
  }

  @PostConstruct
  public void init() {
    try {
      Path directorioUpload = Paths.get(uploadPath);
      if (!Files.exists(directorioUpload)) {
        Files.createDirectories(directorioUpload);
        log.info("📁 Directorio de uploads creado: {}", directorioUpload.toAbsolutePath());
      } else {
        log.info("📁 Directorio de uploads existe: {}", directorioUpload.toAbsolutePath());
      }
      // Listar archivos existentes (para debug)
      long count = Files.list(directorioUpload).count();
      log.info("📁 Archivos existentes en uploads: {}", count);
    } catch (IOException e) {
      log.error("❌ Error al crear directorio de uploads: {}", e.getMessage());
    }
  }

  @Override
  @Transactional
  public MultimediaOutputDTO crear(TipoContenido tipo, String url) {
    log.info("📝 Creando multimedia tipo={}, url={}", tipo, url);

    Multimedia multimedia = new Multimedia();
    multimedia.setTipoContenido(tipo);

    // Si es tipo TEXTO y la URL no empieza con "/multimedia/archivos/", crear archivo físico
    if (tipo == TipoContenido.TEXTO && !url.startsWith("/multimedia/archivos/")) {
      try {
        // Crear directorio si no existe
        Path directorioUpload = Paths.get(uploadPath);
        if (!Files.exists(directorioUpload)) {
          Files.createDirectories(directorioUpload);
        }

        // Generar nombre único para el archivo de texto
        String nombreUnico = UUID.randomUUID().toString() + ".txt";
        Path rutaArchivo = directorioUpload.resolve(nombreUnico);

        // Escribir el contenido de texto al archivo
        Files.write(rutaArchivo, url.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        log.info("📄 Archivo de texto creado: {}", rutaArchivo.toAbsolutePath());

        // Actualizar URL a la ruta del archivo
        url = "/multimedia/archivos/" + nombreUnico;
      } catch (IOException e) {
        log.error("❌ Error al crear archivo de texto: {}", e.getMessage());
        throw new RuntimeException("Error al crear archivo de texto", e);
      }
    }

    multimedia.setUrl(url);

    Multimedia savedMultimedia = contenidoRepositorio.saveAndFlush(multimedia);
    log.info("✅ Multimedia guardada id={}, url={}", savedMultimedia.getId(), url);
    return savedMultimedia.toDTO();
  }

  @Transactional(readOnly = true)
  @Override
  public MultimediaOutputDTO encontrarPorId(Long id) {
    return contenidoRepositorio.findById(id)
            .map(Multimedia::toDTO)
            .orElseThrow(() -> new RuntimeException("Multimedia no encontrada con id: " + id));
  }

  @Override
  @Transactional
  public MultimediaOutputDTO subirArchivo(MultipartFile archivo, TipoContenido tipoContenido) throws IOException {
    log.info("📤 Subiendo archivo: {} ({})", archivo.getOriginalFilename(), tipoContenido);

    // Crear directorio si no existe
    Path directorioUpload = Paths.get(uploadPath);
    if (!Files.exists(directorioUpload)) {
      Files.createDirectories(directorioUpload);
      log.info("📁 Directorio creado: {}", directorioUpload.toAbsolutePath());
    }

    // Generar nombre único para el archivo
    String nombreOriginal = archivo.getOriginalFilename();
    String extension = nombreOriginal != null && nombreOriginal.contains(".")
        ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
        : "";
    String nombreUnico = UUID.randomUUID().toString() + extension;

    // Guardar archivo físicamente
    Path rutaArchivo = directorioUpload.resolve(nombreUnico);
    Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
    log.info("📁 Archivo guardado: {} ({})", rutaArchivo.toAbsolutePath(), Files.size(rutaArchivo) + " bytes");

    // Guardar referencia en BD
    String urlRelativa = "/multimedia/archivos/" + nombreUnico;
    return crear(tipoContenido, urlRelativa);
  }
}
