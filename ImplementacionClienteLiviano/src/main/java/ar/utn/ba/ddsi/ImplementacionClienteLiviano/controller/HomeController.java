package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.AgregadorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

  private final AgregadorService agregadorService;

  public HomeController(AgregadorService agregadorService) {
    this.agregadorService = agregadorService;
  }

  // Endpoint para la Home Page - acceso público
  @GetMapping("/home")
  public String homePage() {
    // Permitir acceso a todos, el frontend manejará qué mostrar según el rol
    return "homePage"; // templates/homePage.html
  }

  @GetMapping("/403")
  public String accessDeniedPage(Authentication authentication, Model model) {
    return "403";
  }
}