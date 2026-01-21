package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

  @GetMapping("/login")
  public String mostrarLogin(HttpServletRequest request) {
    log.info("Iniciando login");

    // Guardar la URL de referencia si existe
    String referer = request.getHeader("Referer");
    HttpSession session = request.getSession();

    if (referer != null && !referer.contains("/login") && !referer.contains("/logout")
        && !referer.contains("/register")) {
      // Extraer solo el path sin el dominio
      try {
        String path = referer.substring(referer.indexOf(request.getContextPath()));
        session.setAttribute("redirectUrl", path);
        log.info("URL de referencia guardada: {}", path);
      } catch (Exception e) {
        log.warn("No se pudo procesar la URL de referencia: {}", referer);
      }
    }

    return "login"; // Renderiza login.html
  }

  @GetMapping("/register")
  public String mostrarRegistro() {
    log.info("Iniciando registro");
    return "register";
  }

  @GetMapping("/registerAdmin")
  public String mostrarRegistroAdmin() {
    log.info("Iniciando registro de Administrador");
    return "registerAdmin";
  }
}
