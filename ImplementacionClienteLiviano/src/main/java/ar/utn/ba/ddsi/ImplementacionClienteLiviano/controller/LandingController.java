package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ColeccionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ConsultarColeccionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class LandingController {

  private final AgregadorService agregadorService;

  public LandingController(AgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }

  @GetMapping({"/"})
  public String home() {
    return "redirect:/landing";
  }

  @GetMapping({"/landing"})
  public String mostrarLanding(
          @RequestParam(value = "nombre_usuario", required = false) String usuario,
          Model model) {

    List<ColeccionDTO> colecciones = new ArrayList<>();
    List<HechoDTO> hechos = new ArrayList<>();

    try {
      // Obtener solo la primera página con 10 hechos para no saturar el sistema
      ConsultarColeccionDTO coleccionDestacada = agregadorService.obtenerColeccionDestacada(0, 10);

      if (coleccionDestacada != null && coleccionDestacada.getInfo() != null) {
        colecciones.add(coleccionDestacada.getInfo());
        hechos = coleccionDestacada.getHechos() != null ? coleccionDestacada.getHechos() : new ArrayList<>();

        // Log comentado para producción
        // log.info("Colección destacada cargada: '{}' con {} hechos",
        //         coleccionDestacada.getInfo().getTitulo(),
        //         hechos.size());
      } else {
        log.warn("Colección destacada vacía desde el backend");
      }
    } catch (Exception e) {
      log.error("Error al cargar colección destacada: {}", e.getMessage());
      // Tips comentados para producción
      // log.info("Asegúrate de que el agregador esté corriendo y las fuentes tengan hechos");
    }

    // Agregamos al modelo para Thymeleaf
    model.addAttribute("colecciones", colecciones);
    model.addAttribute("hechos", hechos);
    model.addAttribute("usuario", usuario);

    return "landingPage";
  }

}
