package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteEstaticaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.FuenteEstaticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class HomeAdminController {

  private final AgregadorService agregadorService;
  private final FuenteEstaticaService fuenteEstaticaService;
  private final FuenteEstaticaClient fuenteEstaticaClient;

  public HomeAdminController(AgregadorService agregadorService,
                             FuenteEstaticaService fuenteEstaticaService,
                             FuenteEstaticaClient fuenteEstaticaClient) {
    this.agregadorService = agregadorService;
    this.fuenteEstaticaService = fuenteEstaticaService;
    this.fuenteEstaticaClient = fuenteEstaticaClient;
  }

  /**
   * @deprecated Redirige a /home. AdminHome está deprecado.
   */
  @GetMapping()
  public String homeAdminPage() {
    return "redirect:/home";
  }

  @GetMapping("/importar")
  public String importarPage() {
    return "importar";
  }

  @GetMapping("/test-import")
  public String testImportPage() {
    return "test-import";
  }

  @PostMapping("/importar")
  public String importarHechos(@RequestParam("archivo") MultipartFile archivo,
                               RedirectAttributes redirectAttributes) {
    try {
      if (archivo.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo");
        return "redirect:/admin/importar";
      }

      List<HechoDTO> hechos = fuenteEstaticaService.importarHechos(archivo);
      redirectAttributes.addFlashAttribute("mensaje",
          "Se importaron " + hechos.size() + " hechos correctamente");

      return "redirect:/admin/importar";
    } catch (IOException e) {
      redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo: " + e.getMessage());
      return "redirect:/admin/importar";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error al comunicarse con FuenteEstatica: " + e.getMessage());
      return "redirect:/admin/importar";
    }
  }

  @GetMapping("/importar/progreso/{fuenteId}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> obtenerProgreso(@PathVariable Long fuenteId) {
    Map<String, Object> progreso = fuenteEstaticaClient.obtenerProgresoImportacion(fuenteId);
    if (progreso == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(progreso);
  }

  @PostMapping("/importar/async")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> importarHechosAsync(@RequestParam("archivo") MultipartFile archivo) {
    try {
      if (archivo.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", "Por favor seleccione un archivo"));
      }

      Long fuenteId = fuenteEstaticaService.iniciarImportacion(archivo);

      return ResponseEntity.ok(Map.of(
          "success", true,
          "fuenteId", fuenteId,
          "mensaje", "Importación iniciada"
      ));
    } catch (IOException e) {
      return ResponseEntity.status(500)
          .body(Map.of("error", "Error al procesar el archivo: " + e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(Map.of("error", "Error al comunicarse con FuenteEstatica: " + e.getMessage()));
    }
  }
}
