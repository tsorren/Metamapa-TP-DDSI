package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteProxyClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.ColeccionService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.EstadisticasService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.FuenteConfiguracionService;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.MicroserviceManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/panel")
public class AdminPanelController {

  private final FuenteConfiguracionService fuenteConfiguracionService;
  private final ColeccionService coleccionService;
  private final MicroserviceManagerService microserviceManager;
  private final EstadisticasService estadisticasService;
  private final AgregadorClient agregadorClient;
  private final FuenteProxyClient fuenteProxyClient;

  @Value("${fuenteestatica.url}")
  private String fuenteEstaticaUrl;

  @Value("${fuentedinamica.url}")
  private String fuenteDinamicaUrl;

  @Value("${fuenteproxy.url}")
  private String fuenteProxyUrl;

  @Value("${agregador.url}")
  private String agregadorUrl;


  public AdminPanelController(FuenteConfiguracionService fuenteConfiguracionService,
                              ColeccionService coleccionService,
                              MicroserviceManagerService microserviceManager,
                              EstadisticasService estadisticasService,
                              AgregadorClient agregadorClient,
                              FuenteProxyClient fuenteProxyClient) {
    this.fuenteConfiguracionService = fuenteConfiguracionService;
    this.coleccionService = coleccionService;
    this.microserviceManager = microserviceManager;
    this.estadisticasService = estadisticasService;
    this.agregadorClient = agregadorClient;
    this.fuenteProxyClient = fuenteProxyClient;
  }

  @GetMapping("/microservicios/status")
  @ResponseBody
  public MicroserviceManagerService.MicroserviceStatus obtenerEstadoMicroservicios() {
    return microserviceManager.obtenerEstadoMicroservicios();
  }

  @GetMapping
  public String panelControl(Model model) {
    // Obtener estadísticas reales desde los microservicios
    EstadisticasDTO stats = estadisticasService.obtenerEstadisticasConsolidadas();
    model.addAttribute("stats", stats);

    // Obtener fuentes configuradas
    List<FuenteConfiguracionDTO> fuentes = fuenteConfiguracionService.obtenerTodasLasFuentes();
    model.addAttribute("fuentes", fuentes);

    // Obtener colecciones para el modal
    List<ColeccionSimpleDTO> colecciones = coleccionService.obtenerColeccionesParaAdmin();
    model.addAttribute("colecciones", colecciones);

    // Obtener estado de los microservicios
    MicroserviceManagerService.MicroserviceStatus microserviceStatus =
            microserviceManager.obtenerEstadoMicroservicios();
    model.addAttribute("microserviceStatus", microserviceStatus);

    return "panelControl";
  }


  /**
   * Endpoint para obtener colecciones para el dropdown del modal
   */
  @GetMapping("/colecciones")
  @ResponseBody
  public List<ColeccionSimpleDTO> obtenerColecciones() {
    return coleccionService.obtenerColeccionesParaAdmin();
  }

  @PostMapping("/fuentes/configurar")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> configurarFuente(
          @RequestParam("tipoFuente") String tipoFuente,
          @RequestParam("nombreFuente") String nombreFuente,
          @RequestParam("coleccionId") Long coleccionId,
          @RequestParam(value = "archivoCsv", required = false) MultipartFile archivoCsv,
          @RequestParam(value = "urlApi", required = false) String urlApi,
          @RequestParam(value = "frecuenciaActualizacion", required = false) Integer frecuencia,
          @RequestParam(value = "urlProxy", required = false) String urlProxy,
          @RequestParam(value = "headersProxy", required = false) String headersProxy) {

    try {
      FuenteConfiguracionDTO fuente = null;

      switch (tipoFuente) {
        case "ESTATICA":
          if (archivoCsv == null || archivoCsv.isEmpty()) {
            throw new IllegalArgumentException("Archivo CSV requerido para fuente estática");
          }
          fuente = fuenteConfiguracionService.configurarFuenteEstatica(nombreFuente, coleccionId, archivoCsv);
          break;
        case "DINAMICA":
          throw new UnsupportedOperationException("Fuente dinámica no implementada aún");
        case "PROXY":
          throw new UnsupportedOperationException("Fuente proxy no implementada aún");
        default:
          throw new IllegalArgumentException("Tipo de fuente no válido: " + tipoFuente);
      }

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente configurada correctamente. Se importaron " +
              (fuente.getCantidadHechos() != null ? fuente.getCantidadHechos() : 0) + " hechos.");
      response.put("fuente", fuente);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al configurar la fuente: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  @PatchMapping("/fuentes/{id}/reimportar")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> reimportarHechos(@PathVariable Long id) {
    try {
      FuenteConfiguracionDTO fuente = fuenteConfiguracionService.reimportarHechos(id);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Hechos reimportados correctamente");
      response.put("fuente", fuente);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al reimportar: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  @GetMapping("/fuentes/{id}/hechos")
  @ResponseBody
  public List<HechoDTO> obtenerHechosDeFuente(@PathVariable Long id) {
    return fuenteConfiguracionService.obtenerHechosDeFuente(id);
  }

  @DeleteMapping("/fuentes/{id}")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> eliminarFuente(@PathVariable Long id) {
    try {
      fuenteConfiguracionService.eliminarFuente(id);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente eliminada correctamente");

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al eliminar la fuente: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * Endpoints para levantar fuentes en el agregador
   */
  @PostMapping("/agregador/fuentes/levantar/estatica")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> levantarFuenteEstatica() {
    try {
      ModuloFuenteOutputDTO modulo = agregadorClient.levantarFuente(fuenteEstaticaUrl, "ESTATICA");

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente Estática levantada correctamente");
      response.put("modulo", modulo);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al levantar Fuente Estática: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/agregador/fuentes/levantar/dinamica")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> levantarFuenteDinamica() {
    try {
      ModuloFuenteOutputDTO modulo = agregadorClient.levantarFuente(fuenteDinamicaUrl, "DINAMICA");

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente Dinámica levantada correctamente");
      response.put("modulo", modulo);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al levantar Fuente Dinámica: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/agregador/fuentes/levantar/proxy")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> levantarFuenteProxy() {
    try {
      ModuloFuenteOutputDTO modulo = agregadorClient.levantarFuente(fuenteProxyUrl, "PROXY");

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente Proxy levantada correctamente");
      response.put("modulo", modulo);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al levantar Fuente Proxy: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  @GetMapping("/agregador/fuentes/registradas")
  @ResponseBody
  public List<FuenteOutputDTO> obtenerFuentesRegistradas() {
    try {
      return agregadorClient.obtenerFuentesRegistradas();
    } catch (Exception e) {
      return List.of();
    }
  }

  @GetMapping("/agregador/modulos")
  @ResponseBody
  public List<ModuloFuenteOutputDTO> obtenerModulosRegistrados() {
    try {
      return agregadorClient.obtenerModulosRegistrados();
    } catch (Exception e) {
      return List.of();
    }
  }

  /**
   * Endpoint para configurar fuente proxy recursiva (MetaMapa)
   */
  @PostMapping("/proxy/configurar/recursiva")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> configurarFuenteProxyRecursiva(
          @RequestParam("urlMetamapa") String url,
          @RequestParam("nombreMetamapa") String nombre) {
    try {
      Map<String, Object> resultado = fuenteProxyClient.crearFuenteProxyRecursiva(url, nombre);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente Proxy Recursiva (MetaMapa) configurada correctamente");
      response.put("data", resultado);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al configurar Fuente Proxy Recursiva: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * Endpoint para configurar fuente proxy externa (DesastreAPI)
   */
  @PostMapping("/proxy/configurar/externa")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> configurarFuenteProxyExterna(
          @RequestParam("urlDesastreApi") String url,
          @RequestParam("emailDesastreApi") String email,
          @RequestParam("passwordDesastreApi") String password,
          @RequestParam("nombreDesastreApi") String nombre) {
    try {
      Map<String, Object> resultado = fuenteProxyClient.crearFuenteProxyExterna(url, email, password, nombre);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "Fuente Proxy Externa (DesastreAPI) configurada correctamente");
      response.put("data", resultado);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "Error al configurar Fuente Proxy Externa: " + e.getMessage());

      return ResponseEntity.badRequest().body(response);
    }
  }
}
