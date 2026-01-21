package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.VisualizarHechosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/visualizarHechos")
public class VisualizarHechosController {

  private final AgregadorService agregadorService;
  private final VisualizarHechosService visualizarHechosService;

  public VisualizarHechosController(AgregadorService agregadorService, VisualizarHechosService visualizarHechosService) {
    this.agregadorService = agregadorService;
    this.visualizarHechosService = visualizarHechosService;
  }

  @GetMapping
  public String consultarColeccion(
          @RequestParam(required = false) String coleccion,
          @RequestParam(required = false) String categoria,
          @RequestParam(value = "tipo_origen", required = false) String tipoOrigen,
          @RequestParam(value = "fecha_reporte_desde", required = false) LocalDateTime fechaReporteDesde,
          @RequestParam(value = "fecha_reporte_hasta", required = false) LocalDateTime fechaReporteHasta,
          @RequestParam(value = "fecha_acontecimiento_desde", required = false) LocalDate fechaAcontecimientoDesde,
          @RequestParam(value = "fecha_acontecimiento_hasta", required = false) LocalDate fechaAcontecimientoHasta,
          @RequestParam(required = false) Double latitud,
          @RequestParam(required = false) Double longitud,
          @RequestParam(required = false) Double radio,
          @RequestParam(value = "navegacion_curada", required = false, defaultValue = "false") boolean navegacionCurada,
          Model model
  ) {
    String handle = (coleccion != null) ? coleccion : "default";

    // Logs de filtros comentados para producción
    // log.info("📍 Consultando colección '{}' con filtros aplicados:", handle);
    // log.info("   - Categoría: {}", categoria != null ? categoria : "todas");
    // log.info("   - Tipo origen: {}", tipoOrigen != null ? tipoOrigen : "todos");
    // log.info("   - Ubicación: {}, {}, radio: {} km", latitud, longitud, radio);

    InformacionFiltrado filtrado = new InformacionFiltrado(
            categoria,
            tipoOrigen,
            fechaReporteDesde,
            fechaReporteHasta,
            fechaAcontecimientoDesde,
            fechaAcontecimientoHasta,
            latitud,
            longitud,
            radio,
            navegacionCurada);

    ConsultarColeccionDTO consulta = agregadorService.consultarColeccion(handle, filtrado);

    // Log comentado para producción
    // log.info("✅ Hechos recibidos después de filtros: {}", consulta.getHechos().size());

    // Pasar "info" en lugar de "coleccion" para que coincida con el HTML
    model.addAttribute("hechos", consulta.getHechos());
    model.addAttribute("info", consulta.getInfo());
    model.addAttribute("navegacionCurada", navegacionCurada);

    return "visualizarHechos"; // templates/visualizarHechos.html
  }

  @GetMapping("/api/hechos")
  @ResponseBody
  public ConsultarColeccionDTO consultarColeccion(
          @RequestParam(required = false) String coleccion,
          @RequestParam(required = false) String categoria,
          @RequestParam(value = "tipo_origen", required = false) String tipoOrigen,
          @RequestParam(value = "fecha_reporte_desde", required = false) LocalDateTime fechaReporteDesde,
          @RequestParam(value = "fecha_reporte_hasta", required = false) LocalDateTime fechaReporteHasta,
          @RequestParam(value = "fecha_acontecimiento_desde", required = false) LocalDate fechaAcontecimientoDesde,
          @RequestParam(value = "fecha_acontecimiento_hasta", required = false) LocalDate fechaAcontecimientoHasta,
          @RequestParam(required = false) Double latitud,
          @RequestParam(required = false) Double longitud,
          @RequestParam(required = false) Double radio,
          @RequestParam(value = "navegacion_curada", required = false, defaultValue = "false") boolean navegacionCurada
  ) {
    String handle = (coleccion != null) ? coleccion : "default";
    InformacionFiltrado filtrado = new InformacionFiltrado(
            categoria,
            tipoOrigen,
            fechaReporteDesde,
            fechaReporteHasta,
            fechaAcontecimientoDesde,
            fechaAcontecimientoHasta,
            latitud,
            longitud,
            radio,
            navegacionCurada);

    return agregadorService.consultarColeccion(handle, filtrado);
  }

  /**
   * Solicitar eliminación de un hecho desde la página visualizarHechos.
   * El ID recibido es el ID interno de Agregador (no necesita mapeo).
   */
  @PostMapping("/api/eliminar")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> solicitarEliminacion(@RequestBody SolicitudEliminacionInputDTO solicitud, @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      if(userDetails != null) {
        solicitud.setIdContribuyente(userDetails.getId());
      }

      SolicitudDeEliminacionOutputDTO solicitudCreada = visualizarHechosService.solicitarEliminacion(solicitud);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Solicitud de eliminación enviada correctamente");
      response.put("solicitud", solicitudCreada);

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Validación fallida: {}", e.getMessage());
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    } catch (Exception e) {
      log.error("Error al solicitar eliminación: {}", e.getMessage());
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al procesar la solicitud: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }
}