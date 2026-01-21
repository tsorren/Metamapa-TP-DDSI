package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ColeccionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/colecciones")
public class ColeccionesController {

  private final AgregadorService agregadorService;

  public ColeccionesController(AgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }

  @GetMapping
  public String listarColecciones(Model model) {
    List<ColeccionDTO> colecciones = agregadorService.listarColecciones();
    model.addAttribute("colecciones", colecciones);
    return "coleccionesDisponibles";
  }
}
