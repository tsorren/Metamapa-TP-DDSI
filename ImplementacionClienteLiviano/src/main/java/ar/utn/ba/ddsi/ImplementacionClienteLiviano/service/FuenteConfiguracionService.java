package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteEstaticaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ColeccionSimpleDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.FuenteConfiguracionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FuenteConfiguracionService {

  private final FuenteEstaticaClient fuenteEstaticaClient;
  private final ColeccionService coleccionService;
  private final List<FuenteConfiguracionDTO> fuentesConfiguradas;
  private final AtomicLong idGenerator = new AtomicLong(1);

  public FuenteConfiguracionService(FuenteEstaticaClient fuenteEstaticaClient,
                                    ColeccionService coleccionService) {
    this.fuenteEstaticaClient = fuenteEstaticaClient;
    this.coleccionService = coleccionService;
    this.fuentesConfiguradas = new ArrayList<>();
  }

  /**
   * Configura una nueva fuente estática
   */
  public FuenteConfiguracionDTO configurarFuenteEstatica(String nombre, Long coleccionId,
                                                         MultipartFile archivo) throws IOException {
    // Verificar que la colección existe
    ColeccionSimpleDTO coleccion = obtenerColeccionPorId(coleccionId);
    if (coleccion == null) {
      throw new IllegalArgumentException("Colección no encontrada con id: " + coleccionId);
    }

    // Subir archivo al microservicio y procesarlo automáticamente
    var response = fuenteEstaticaClient.subirYProcesarCSV(archivo);

    if (response.getBody() != null && response.getBody().containsKey("identificador")) {
      Long fuenteId = ((Number) response.getBody().get("identificador")).longValue();

      // Crear configuración local
      FuenteConfiguracionDTO configuracion = new FuenteConfiguracionDTO(
              idGenerator.getAndIncrement(),
              nombre,
              "ESTATICA",
              coleccionId,
              LocalDateTime.now(),
              "Archivo: " + archivo.getOriginalFilename(),
              fuenteId // ID del microservicio FuenteEstatica
      );

      // Enriquecer con datos de la colección
      configuracion.setNombreColeccion(coleccion.getNombre());
      configuracion.setHandleColeccion(coleccion.getHandle());
      configuracion.setEstadoImportacion("PROCESADO");

      // Obtener cantidad de hechos importados
      try {
        List<HechoDTO> hechos = fuenteEstaticaClient.obtenerHechosDeFuente(fuenteId, null);
        configuracion.setCantidadHechos(hechos.size());
      } catch (Exception e) {
        configuracion.setCantidadHechos(0);
      }

      fuentesConfiguradas.add(configuracion);
      return configuracion;
    }

    throw new RuntimeException("Error al configurar la fuente estática");
  }

  /**
   * Reimpota los hechos de una fuente existente
   */
  public FuenteConfiguracionDTO reimportarHechos(Long configuracionId) {
    FuenteConfiguracionDTO config = fuentesConfiguradas.stream()
            .filter(f -> f.getId().equals(configuracionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada"));

    try {
      List<HechoDTO> hechos = fuenteEstaticaClient.importarHechos(config.getFuenteExternaId());
      config.setCantidadHechos(hechos.size());
      config.setEstadoImportacion("REIMPORTADO - " + LocalDateTime.now().toString());
      return config;
    } catch (Exception e) {
      config.setEstadoImportacion("ERROR AL REIMPORTAR");
      throw new RuntimeException("Error al reimportar hechos: " + e.getMessage());
    }
  }

  private ColeccionSimpleDTO obtenerColeccionPorId(Long id) {
    return coleccionService.obtenerColeccionesParaAdmin()
            .stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElse(null);
  }

  /**
   * Obtiene todas las fuentes configuradas
   */
  public List<FuenteConfiguracionDTO> obtenerTodasLasFuentes() {
    // Actualizar estado de cada fuente
    for (FuenteConfiguracionDTO fuente : fuentesConfiguradas) {
      if ("ESTATICA".equals(fuente.getTipo()) && fuente.getFuenteExternaId() != null) {
        try {
          List<HechoDTO> hechos = fuenteEstaticaClient.obtenerHechosDeFuente(
                  fuente.getFuenteExternaId(), null);
          fuente.setCantidadHechos(hechos.size());
        } catch (Exception e) {
          // Si hay error, mantener el último estado conocido
        }
      }
    }
    return new ArrayList<>(fuentesConfiguradas);
  }

  /**
   * Elimina una fuente configurada
   */
  public void eliminarFuente(Long id) {
    fuentesConfiguradas.removeIf(f -> f.getId().equals(id));
    // TODO: Considerar si eliminar también del microservicio
  }

  /**
   * Obtiene los hechos de una fuente específica
   */
  public List<HechoDTO> obtenerHechosDeFuente(Long configuracionId) {
    FuenteConfiguracionDTO config = fuentesConfiguradas.stream()
            .filter(f -> f.getId().equals(configuracionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Configuración no encontrada"));

    if (config.getFuenteExternaId() != null) {
      return fuenteEstaticaClient.obtenerHechosDeFuente(config.getFuenteExternaId(), null);
    }

    return new ArrayList<>();
  }
}
