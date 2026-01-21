package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.middlewares;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  // Lista de rutas que REQUIEREN autenticación
  private static final List<String> PROTECTED_PATHS = Arrays.asList(
      "/admin",
      "/auth/roles-permisos"
  );

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();
    String method = request.getMethod();

    log.debug("[JWT FILTER] Procesando {} {}", method, path);

    String header = request.getHeader("Authorization");

    // Si hay un header Authorization con Bearer token, intentar validarlo
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      log.debug("Token encontrado, validando...");
      try {
        String username = JwtUtil.validarToken(token);
        Claims claims = JwtUtil.obtenerClaims(token);
        String rol = claims.get("rol", String.class);

        var auth = new UsernamePasswordAuthenticationToken(
            username,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("Token valido para usuario: {} con rol: {}", username, rol);
      } catch (Exception e) {
        // Token inválido - limpiar contexto pero NO bloquear
        SecurityContextHolder.clearContext();
        log.debug("Token invalido (ignorando para rutas publicas): {}", e.getMessage());
      }
    } else {
      log.debug("Sin header Authorization para: {}", path);
    }

    // Siempre continuar con la cadena de filtros
    // Spring Security decidirá si la ruta requiere autenticación o no
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // NUNCA saltear el filtro - siempre dejarlo pasar
    // El filtro solo intenta autenticar si hay token, no bloquea nada
    return false;
  }
}
