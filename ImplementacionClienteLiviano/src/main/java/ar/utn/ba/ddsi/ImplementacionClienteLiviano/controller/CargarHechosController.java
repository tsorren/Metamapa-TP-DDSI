package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoInputDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cargarHecho")
public class CargarHechosController {

  private final AgregadorService agregadorService;
  private final FuenteDinamicaClient fuenteDinamicaClient;

  public CargarHechosController(AgregadorService agregadorService, FuenteDinamicaClient fuenteDinamicaClient) {
    this.agregadorService = agregadorService;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
  }

  @GetMapping
  public String cargarHecho(
          @RequestParam(value = "coleccion", required = false) String coleccionId,
          @RequestParam(value = "nombre_usuario", required = false) String usuario,
          Model model) {

    // Cargar hechos existentes si hay una colección específica
    List<HechoDTO> hechos = List.of(); // agregadorService.listarHechosDeColeccion(coleccionId) cuando esté implementado

    model.addAttribute("hechos", hechos);
    model.addAttribute("coleccionId", coleccionId);
    model.addAttribute("usuario", usuario);

    return "cargarHecho";
  }

  @PostMapping()
  @ResponseBody
  public ResponseEntity<HechoDTO> crearHecho(
          @RequestBody HechoInputDTO hechoInput,
          @AuthenticationPrincipal CustomUserDetails userDetails) {
    try {
      // Verificar que el usuario esté autenticado
      if (userDetails == null) {
        log.warn("Intento de crear hecho sin autenticación");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Asignar el ID del contribuyente desde la sesión
      hechoInput.setIdContribuyente(userDetails.getId());

      HechoDTO hechoCreado = fuenteDinamicaClient.crearHecho(hechoInput);
      log.info("Hecho creado por contribuyente id={}", userDetails.getId());
      return ResponseEntity.ok(hechoCreado);

    } catch (Exception e) {
      log.error("Error al crear hecho: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }
}