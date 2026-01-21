package ar.utn.ba.ddsi.ImplementacionClienteLiviano.controller;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

  //private final AuthenticationManager authenticationManager;
  private final UsuarioService usuarioService;

  public AuthController(/*AuthenticationManager authenticationManager,*/ UsuarioService usuarioService) {
    //this.authenticationManager = authenticationManager;
    this.usuarioService = usuarioService;
  }

  /*@PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
    try{
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
      Authentication auth = authenticationManager.authenticate(authToken);
      SecurityContextHolder.getContext().setAuthentication(auth);

      SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
      securityContext.setAuthentication(authToken);
      SecurityContextHolder.setContext(securityContext);

      // ✅ Guardar contexto en la sesión
      HttpSession session = request.getSession(true);
      session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

      // Obtener el rol del usuario autenticado
      String role = auth.getAuthorities().stream()
          .findFirst()
          .map(authority -> authority.getAuthority())
          .orElse("ROLE_USER");

      Map<String, String> response = new HashMap<>();
      response.put("message", "Login exitoso");
      response.put("name", loginDTO.getUsername());
      response.put("role", role);

      return ResponseEntity.ok(response);
    }catch (Exception e){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Credenciales inválidas"));
    }
  }*/
  /*@GetMapping("/login")
  public String mostrarLogin() {
    return "login"; // Renderiza login.html
  }*/


  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDTO registerDTO) {
    try{
      ContribuyenteOutputDTO confirmedContribuyente = usuarioService.registrarContribuyente(registerDTO);
      return ResponseEntity.ok(Map.of("message", "Registro exitoso", "name", confirmedContribuyente.getUsername()));
    }
    catch (RuntimeException e) {
      // Intentar parsear el JSON recibido del otro backend
      String errorMessage = "Error desconocido";
      try {
        var json = new ObjectMapper().readValue(e.getMessage(), Map.class);
        if (json.containsKey("error")) {
          errorMessage = json.get("error").toString();
        }
      } catch (Exception parseError) {
        errorMessage = e.getMessage(); // por si no era JSON válido
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", errorMessage));
    }
  }
  @PostMapping("/registerAdmin")
  public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody RegisterAdminDTO registerDTO) {
    try{
      AdministradorOutputDTO confirmedAdministrator = usuarioService.registrarAdministrador(registerDTO);
      return ResponseEntity.ok(Map.of("message", "Registro exitoso", "name", confirmedAdministrator.getUsername()));
    }
    catch (RuntimeException e) {
      // Intentar parsear el JSON recibido del otro backend
      String errorMessage = "Error desconocido";
      try {
        var json = new ObjectMapper().readValue(e.getMessage(), Map.class);
        if (json.containsKey("error")) {
          errorMessage = json.get("error").toString();
        }
      } catch (Exception parseError) {
        errorMessage = e.getMessage(); // por si no era JSON válido
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", errorMessage));
    }
  }

  @PostMapping("/recuperar")
  @ResponseBody
  public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody RecuperarDTO recuperarDTO) {
    // TODO: Conectar con backend - recuperarDTO.username o email

    // Simulación de respuesta exitosa por ahora
    Map<String, String> response = new HashMap<>();
    response.put("message", "Enlace de recuperación enviado");
    response.put("status", "success");

    return ResponseEntity.ok(response);
  }
}
