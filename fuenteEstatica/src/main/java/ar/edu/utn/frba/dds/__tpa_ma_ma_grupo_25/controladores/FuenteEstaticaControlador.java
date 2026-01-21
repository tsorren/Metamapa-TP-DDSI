package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteEstaticaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteEstaticaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteEstaticaServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ImportacionProgressTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fuentes")
//@CrossOrigin(origins = "*") // Permitir CORS para el cliente
public class FuenteEstaticaControlador {

  private final IFuenteEstaticaServicio fuenteEstaticaServicio;
  private final ImportacionProgressTracker progressTracker;

  public FuenteEstaticaControlador(IFuenteEstaticaServicio fuenteEstaticaServicio,
                                   ImportacionProgressTracker progressTracker) {
    this.fuenteEstaticaServicio = fuenteEstaticaServicio;
    this.progressTracker = progressTracker;
  }

  @PostMapping()
  public FuenteOutputDTO crearFuente(@RequestBody FuenteEstaticaInputDTO inputDTO) {
    return fuenteEstaticaServicio.crearFuente(Path.of(inputDTO.getPath()));
  }

  /**
   * Endpoint para subir archivos CSV directamente y procesarlos automáticamente
   */
  @PostMapping("/upload")
  public ResponseEntity<FuenteOutputDTO> subirYCrearFuente(@RequestParam("file") MultipartFile archivo) {
    return fuenteEstaticaServicio.subirYCrearFuente(archivo);
  }

  @GetMapping()
  public List<FuenteOutputDTO> consultarFuentes(
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {
    if (fechaAnteriorA != null) {
      return fuenteEstaticaServicio.encontrarTodas().stream()
              .filter(f -> f.getFechaDeCreacion().isAfter(fechaAnteriorA))
              .toList();
    }
    return fuenteEstaticaServicio.encontrarTodas();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<List<HechoOutputDTO>> importarHechos(@PathVariable("id") Long identificadorFuente) {
    try {
      List<HechoOutputDTO> hechos = fuenteEstaticaServicio.importarHechos(identificadorFuente);
      return ResponseEntity.ok(hechos);
    } catch (Exception e) {
      log.error("Error al importar hechos de fuente {}: {}", identificadorFuente, e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<HechoOutputDTO>> hechosDeFuente(@PathVariable("id") Long id,
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {
    try {
      List<HechoOutputDTO> hechos;
      if (fechaAnteriorA != null) {
        hechos = fuenteEstaticaServicio.hechosDeFuente(id).stream()
                .filter(h -> h.getFechaDeCarga().isAfter(fechaAnteriorA) ||
                        h.getFechaUltimaModificacion().isAfter(fechaAnteriorA)).toList();
      } else {
        hechos = fuenteEstaticaServicio.hechosDeFuente(id);
      }
      return ResponseEntity.ok(hechos);
    } catch (Exception e) {
      log.error("Error al obtener hechos de fuente {}: {}", id, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @GetMapping("/admin/estadisticas")
  public EstadisticasFuenteEstaticaDTO obtenerEstadisticas() {
    log.info("<REQUEST> Solicitando estadísticas de FuenteEstatica");
    return fuenteEstaticaServicio.obtenerEstadisticas();
  }

  @GetMapping("/progreso/{fuenteId}")
  public ResponseEntity<ImportacionProgressTracker.ProgresoImportacion> obtenerProgreso(@PathVariable Long fuenteId) {
    ImportacionProgressTracker.ProgresoImportacion progreso = progressTracker.obtenerProgreso(fuenteId);
    if (progreso == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(progreso);
  }

}
