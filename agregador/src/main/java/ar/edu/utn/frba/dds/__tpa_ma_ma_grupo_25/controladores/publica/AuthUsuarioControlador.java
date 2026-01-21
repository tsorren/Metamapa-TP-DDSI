package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones.NotFoundException;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.AuthResponseDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.RefreshRequest;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.RolesPermisosDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.TokenResponse;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Usuario;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ILoginService;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Map;


@RestController
@RequestMapping("/usuarios/auth")
public class AuthUsuarioControlador {
  private static final Logger log = LoggerFactory.getLogger(AuthUsuarioControlador.class);
  private final ILoginService loginService;

  public AuthUsuarioControlador(ILoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping
  public ResponseEntity<AuthResponseDTO> loginApi(@RequestBody Map<String, String> credentials) {
    try {
      String username = credentials.get("username");
      String password = credentials.get("password");

      // Validación básica de credenciales
      if (username == null || username.trim().isEmpty() ||
          password == null || password.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      // Autenticar usuario usando el LoginService
      Usuario user = loginService.autenticarUsuario(username, password);

      // Generar tokens
      String accessToken = loginService.generarAccessToken(username);
      String refreshToken = loginService.generarRefreshToken(username);

      AuthResponseDTO response = AuthResponseDTO.builder()
          .accessToken(accessToken)
          .refreshToken(refreshToken)
          .id(user.getId())
          .build();

      log.info("El usuario {} está logueado. El token generado es {}", username, accessToken);

      return ResponseEntity.ok(response);
    } catch (NotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
    try {
      String username = JwtUtil.validarToken(request.getRefreshToken());

      // Validar que el token sea de tipo refresh
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(JwtUtil.getKey())
          .build()
          .parseClaimsJws(request.getRefreshToken())
          .getBody();

      if (!"refresh".equals(claims.get("type"))) {
        return ResponseEntity.badRequest().build();
      }

      String newAccessToken = loginService.generarAccessToken(username);
      TokenResponse response = new TokenResponse(newAccessToken, request.getRefreshToken());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/roles-permisos")
  public ResponseEntity<RolesPermisosDTO> getUserRolesAndPermissions(Authentication authentication) {
    try {
      String username = authentication.getName();
      RolesPermisosDTO response = loginService.obtenerRolesYPermisosUsuario(username);
      return ResponseEntity.ok(response);
    } catch (NotFoundException e) {
      log.error("Usuario no encontrado", e);
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error("Error al obtener roles y permisos del usuario", e);
      return ResponseEntity.badRequest().build();
    }
  }
}

