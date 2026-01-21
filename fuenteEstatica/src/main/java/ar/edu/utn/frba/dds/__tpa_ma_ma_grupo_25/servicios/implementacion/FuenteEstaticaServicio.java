package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteEstaticaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteEstaticaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteEstaticaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FuenteEstaticaServicio implements IFuenteEstaticaServicio {
  private final IFuenteEstaticaRepositorioBD fuenteRepositorio;
  private final ImportacionAsincronaServicio importacionAsincronaServicio;
  private final String uploadDir;

  public FuenteEstaticaServicio(IFuenteEstaticaRepositorioBD fuenteRepositorio,
                                ImportacionAsincronaServicio importacionAsincronaServicio,
                                @Value("${app.upload.dir:uploads/csv}") String uploadDir) {
    this.fuenteRepositorio = fuenteRepositorio;
    this.importacionAsincronaServicio = importacionAsincronaServicio;
    this.uploadDir = uploadDir;
  }

  @Override
  public FuenteOutputDTO crearFuente(Path rutaArchivo) {
    // Primero guardar la fuente en una transacción separada
    FuenteOutputDTO resultado = guardarFuente(rutaArchivo);

    // DESPUÉS de que la transacción se complete, iniciar importación asíncrona
    // Esto garantiza que la fuente exista en la BD cuando el thread async la busque
    importacionAsincronaServicio.importarHechosAsync(resultado.getIdentificador());
    log.info("<CREACION> Importación iniciada en segundo plano para fuente {}", resultado.getIdentificador());

    return resultado;
  }

  /**
   * Guarda la fuente en una transacción. Este método debe completarse
   * ANTES de llamar al método async para evitar race conditions.
   */
  @Transactional
  public FuenteOutputDTO guardarFuente(Path rutaArchivo) {
    if (rutaArchivo == null) {
      throw new IllegalArgumentException("La ruta del archivo no puede ser null");
    }

    // Para archivos subidos, la ruta ya es absoluta
    Path rutaAbsoluta = rutaArchivo.isAbsolute() ? rutaArchivo : rutaArchivo.toAbsolutePath();

    if (!Files.exists(rutaAbsoluta)) {
      throw new IllegalArgumentException("El archivo no existe: " + rutaAbsoluta);
    }

    log.info("Creando fuente con ruta: {}", rutaAbsoluta);

    try {
      FuenteEstatica fuente = new FuenteEstatica(rutaAbsoluta);

      // GUARDAR LA FUENTE PRIMERO para que tenga un ID
      fuente.setFechaDeCreacion(LocalDateTime.now());
      fuente = fuenteRepositorio.save(fuente);
      log.info("<CREACION> Fuente creada con ID: {}", fuente.getId());


      return new FuenteOutputDTO(
              fuente.getId(),
              fuente.getFechaDeCreacion()
      );
    } catch (Exception e) {
      log.error("Error al crear fuente: {}", e.getMessage());
      throw new RuntimeException("Error al crear la fuente: " + e.getMessage());
    }
  }

  @Override
  public ResponseEntity<FuenteOutputDTO> subirYCrearFuente(MultipartFile archivo) {
    try {
      if (archivo.isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      log.info("Recibiendo archivo: {}, tamaño: {} bytes",
              archivo.getOriginalFilename(), archivo.getSize());

      // Crear directorio si no existe
      Path uploadsDir = Paths.get(uploadDir);
      Files.createDirectories(uploadsDir);

      // Generar nombre único para evitar colisiones
      String nombreUnico = UUID.randomUUID() + "-" + archivo.getOriginalFilename();
      Path rutaArchivo = uploadsDir.resolve(nombreUnico);

      // Guardar archivo
      Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
      log.info("Archivo guardado en: {}", rutaArchivo.toAbsolutePath());

      // Crear fuente con el archivo subido (esto ya procesa automáticamente los hechos)
      FuenteOutputDTO resultado = this.crearFuente(rutaArchivo);

      log.info("Fuente creada exitosamente con ID: {}", resultado.getIdentificador());
      return ResponseEntity.ok(resultado);

    } catch (IOException e) {
      log.error("Error de E/O al procesar archivo: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
      log.error("Error inesperado al procesar archivo: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<FuenteOutputDTO> encontrarTodas() {
    List<FuenteEstatica> fuentes = fuenteRepositorio.findAll();
    log.info("<CONSULTA> {} Fuentes disponibles", fuentes.size());
    return fuentes.stream().map(f -> new FuenteOutputDTO(
            f.getId(),
            f.getFechaDeCreacion()
    )).toList();
  }

  @Override
  public List<HechoOutputDTO> importarHechos(Long fuenteId) {
    // Delegar al servicio de importación asíncrona (modo síncrono)
    return importacionAsincronaServicio.importarHechosSync(fuenteId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HechoOutputDTO> hechosDeFuente(Long identificador) {
    try {
      FuenteEstatica fuente = fuenteRepositorio.findById(identificador)
              .orElseThrow(() -> new IllegalArgumentException("Fuente no encontrada con id: " + identificador));

      List<Hecho> hechos = fuente.getHechos();
      log.info("<CONSULTA> Fuente {} respondió con {} hechos", identificador, hechos.size());

      return hechos.stream()
              .map(HechoServicio::hechoOutputDTO)
              .toList();
    } catch (Exception e) {
      log.error("Error al obtener hechos de fuente {}: {}", identificador, e.getMessage());
      throw new RuntimeException("Error al obtener hechos: " + e.getMessage());
    }
  }
  @Override
  @Transactional(readOnly = true)
  public EstadisticasFuenteEstaticaDTO obtenerEstadisticas() {
    List<FuenteEstatica> fuentes = fuenteRepositorio.findAll();

    int totalFuentes = fuentes.size();
    int totalHechos = 0;

    // Contar todos los hechos de todas las fuentes
    for (FuenteEstatica fuente : fuentes) {
      try {
        totalHechos += fuente.getHechos().size();
      } catch (Exception e) {
        log.warn("Error al obtener hechos de fuente {}: {}", fuente.getId(), e.getMessage());
      }
    }

    log.info("<ESTADISTICAS> Fuentes creadas: {}, Hechos importados: {}", totalFuentes, totalHechos);
    return new EstadisticasFuenteEstaticaDTO(totalFuentes, totalHechos);
  }

}
