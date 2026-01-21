package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AdminColeccionesService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class AdminColeccionesController {

  private final AgregadorService agregadorService;
  private final AdminColeccionesService adminColeccionesService;

  public AdminColeccionesController(AgregadorService agregadorService, AdminColeccionesService adminColeccionesService) {
    this.agregadorService = agregadorService;
    this.adminColeccionesService = adminColeccionesService;
  }

  @GetMapping("/admin/colecciones")
  //@PreAuthorize("hasRole('ADMIN')") //verifica que el usuario tenga el rol de admin
  public String mostrarAdminColecciones(Model model) {
    List<ColeccionDTO> colecciones = agregadorService.listarColecciones();
    model.addAttribute("colecciones", colecciones);
    List<FuenteOutputDTO> fuentes = agregadorService.listarFuentes();
    //mock para probar
    //fuentes.add(new FuenteOutputDTO(1l, 1l, new ModuloFuenteOutputDTO(1l, "algo.com", "ESTATICO")));
    model.addAttribute("fuentes", fuentes);
    model.addAttribute("fuentesForm", new FuentesDTOForm());
    return "adminColecciones"; // nombre del template .html
  }

  @GetMapping("/admin/colecciones/api")
  @ResponseBody
  public ResponseEntity<List<ColeccionDTO>> listarColeccionesAPI() {
    List<ColeccionDTO> colecciones = agregadorService.listarColecciones();
    return ResponseEntity.ok(colecciones);
  }

  @PostMapping("/admin/colecciones/api")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody ColeccionInputDTO datos) {
    try {
      ColeccionDTO resultado = agregadorService.crearColeccion(datos);
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @PatchMapping("/admin/colecciones/api/{handle}")
  //@PreAuthorize("hasRole('ADMIN')")
  @ResponseBody
  public ResponseEntity<ColeccionDTO> actualizarColeccion(@PathVariable String handle, @RequestBody ColeccionInputDTO datos) {
    try {
      ColeccionDTO resultado = agregadorService.actualizarColeccion(handle, datos);
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @DeleteMapping("/admin/colecciones/api/{handle}")
 // @PreAuthorize("hasRole('ADMIN')")
  @ResponseBody
  public ResponseEntity<?> eliminarColeccion(@PathVariable String handle) {
    try {
      agregadorService.eliminarColeccion(handle);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Crear colección con filtros dinámicos.
   * Recibe una lista de filtros en vez de un idCriterio.
   */
  @PostMapping("/admin/colecciones/api/con-filtros")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> crearColeccionConFiltros(@RequestBody CrearColeccionConFiltrosDTO datos) {
    try {
      log.info("Recibida solicitud de creación de colección con {} filtros", 
               datos.getFiltros() != null ? datos.getFiltros().size() : 0);
      
      ColeccionDTO resultado = adminColeccionesService.crearColeccionConFiltros(datos);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Colección creada exitosamente");
      response.put("coleccion", resultado);
      
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Validación fallida: {}", e.getMessage());
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    } catch (Exception e) {
      log.error("Error al crear colección con filtros: {}", e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al crear colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }

  /**
   * Actualizar colección existente con filtros dinámicos.
   * Reemplaza los filtros existentes del criterio con los nuevos.
   */
  @PatchMapping("/admin/colecciones/api/{handle}/con-filtros")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> actualizarColeccionConFiltros(
      @PathVariable String handle,
      @RequestBody ActualizarColeccionConFiltrosDTO datos) {
    try {
      log.info("Recibida solicitud de actualización de colección {} con {} filtros",
               handle, datos.getFiltros() != null ? datos.getFiltros().size() : 0);

      ColeccionDTO resultado = adminColeccionesService.actualizarColeccionConFiltros(handle, datos);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Colección actualizada exitosamente");
      response.put("coleccion", resultado);

      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Validación fallida: {}", e.getMessage());
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    } catch (Exception e) {
      log.error("Error al actualizar colección con filtros: {}", e.getMessage(), e);
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "Error al actualizar colección: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }

  /*@GetMapping("/admin/importar")
  public String importar(Model model) {
    return "importar";
  }*/
}
