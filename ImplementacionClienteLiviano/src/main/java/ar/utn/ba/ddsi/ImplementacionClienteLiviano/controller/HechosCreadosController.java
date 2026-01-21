package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.HechosCreadosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/hechos-creados")
public class HechosCreadosController {

    private final HechosCreadosService hechosCreadosService;

    public HechosCreadosController(HechosCreadosService hechosCreadosService) {
        this.hechosCreadosService = hechosCreadosService;
    }

    /**
     * Renderizar la página de hechos creados
     */
    @GetMapping
    public String mostrarHechosCreados(Model model) {
        return "hechosCreados";
    }

    /**
     * Obtener hechos del contribuyente (AJAX)
     */
    @GetMapping("/api/hechos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerHechosContribuyente(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Verificar autenticación
            if (userDetails == null) {
                log.warn("Intento de acceso a /api/hechos sin autenticación");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<HechoOutputDTO> hechos = hechosCreadosService.obtenerHechosDeContribuyente(userDetails.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hechos", hechos);
            response.put("count", hechos.size());
            
            // Log comentado para producción - descomentar para debug
            // log.info("Hechos cargados para contribuyente {}: {} hechos", userDetails.getId(), hechos.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener hechos del contribuyente: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al cargar los hechos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Verificar si un hecho puede ser modificado
     */
    @GetMapping("/api/puede-modificar/{idHecho}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> puedeModificar(@PathVariable Long idHecho, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<HechoOutputDTO> hechos = hechosCreadosService.obtenerHechosDeContribuyente(userDetails.getId());
            HechoOutputDTO hecho = hechos.stream()
                    .filter(h -> h.getIdentificador().equals(idHecho))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Hecho no encontrado"));

            boolean puedeModificar = hechosCreadosService.puedeModificar(hecho);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("puedeModificar", puedeModificar);
            response.put("fechaDeCarga", hecho.getFechaDeCarga());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al verificar si puede modificar: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Solicitar modificación de un hecho.
     * Recibe todos los datos del formulario (originales + modificados),
     * compara y construye el DTO con solo los campos modificados.
     */
    @PostMapping("/api/modificar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> solicitarModificacion(@RequestBody SolicitudModificacionFormDTO formData, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            formData.setIdContribuyente(userDetails.getId());
            
            SolicitudDeModificacionOutputDTO solicitudCreada = hechosCreadosService.solicitarModificacion(formData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Solicitud de modificación enviada correctamente");
            response.put("solicitud", solicitudCreada);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Validación fallida: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error al solicitar modificación: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al procesar la solicitud: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Solicitar eliminación de un hecho
     */
    @PostMapping("/api/eliminar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> solicitarEliminacion(@RequestBody SolicitudEliminacionInputDTO solicitud, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if(userDetails != null) {
              solicitud.setIdContribuyente(userDetails.getId());
            }
            
            SolicitudDeEliminacionOutputDTO solicitudCreada = hechosCreadosService.solicitarEliminacion(solicitud);
            
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


  @PostMapping("/eliminar")
  public String eliminarHecho(
          @RequestParam Long id,
          @RequestParam String justificacion,
          Model model) {

    // Validaciones de la justificación
    if (justificacion == null || justificacion.trim().isEmpty()) {
      model.addAttribute("error", "La justificación es obligatoria");
      return mostrarHechosCreados(model);
    }

    if (justificacion.trim().length() < 500) {
      model.addAttribute("error", "La justificación debe tener al menos 500 caracteres");
      return mostrarHechosCreados(model);
    }

    if (justificacion.trim().length() > 2000) {
      model.addAttribute("error", "La justificación no puede exceder los 2000 caracteres");
      return mostrarHechosCreados(model);
    }

    // Aquí se procesaría la eliminación en la base de datos
    // - Registrar la justificación en un log de auditoría
    // - Eliminar el hecho con el ID especificado
    // - Eliminar archivos multimedia asociados si existen
    // - Guardar información del usuario que eliminó y timestamp

    // Registro de auditoría
    log.info("Solicitud de eliminación - Hecho id={}, justificación={} chars", id, justificacion.trim().length());

    model.addAttribute("mensaje", "Hecho eliminado correctamente. La eliminación ha sido registrada.");
    return mostrarHechosCreados(model);
  }

  @GetMapping("/{id}")
  public String verHecho(@PathVariable Long id, Model model) {
    // Aquí se buscaría el hecho específico en la base de datos
    // Por ahora redirigimos a la página de visualizar hechos
    return "redirect:/visualizar-hechos/" + id;
  }
}