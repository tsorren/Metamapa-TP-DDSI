package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.middlewares;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    // Si hay un header Authorization con Bearer token
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        String username = JwtUtil.validarToken(token);
        Claims claims = JwtUtil.obtenerClaims(token);
        var auth = new UsernamePasswordAuthenticationToken(
            username,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + claims.get("rol", String.class)))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
        // Token inválido - limpiar contexto de seguridad
        SecurityContextHolder.clearContext();
        // NO enviar error 401 aquí - dejar que Spring Security decida según la configuración
      }
    }

    // Siempre continuar con la cadena de filtros
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // No aplicar el filtro JWT a endpoints públicos
    return path.startsWith("/api/auth")
        || path.startsWith("/actuator")
        || path.startsWith("/estadisticas"); // Permitir acceso público a estadísticas
  }
}
