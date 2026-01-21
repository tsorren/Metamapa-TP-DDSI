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

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();
    String method = request.getMethod();
    // Logs de cada request comentados para producción
    // log.info("🔍 [JWT FILTER - FuenteEstatica] {} {} - Iniciando validación", method, path);

    String header = request.getHeader("Authorization");

    // Logs de header comentados para producción
    // if (header != null) {
    //   log.info("📋 [JWT FILTER] Header Authorization presente: {}", header.substring(0, Math.min(20, header.length())) + "...");
    // } else {
    //   log.info("📋 [JWT FILTER] NO hay header Authorization");
    // }

    // Si hay un header Authorization con Bearer token
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      // log.info("🔑 [JWT FILTER] Token encontrado, validando...");
      try {
        String username = JwtUtil.validarToken(token);
        Claims claims = JwtUtil.obtenerClaims(token);
        var auth = new UsernamePasswordAuthenticationToken(
            username,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + claims.get("rol", String.class)))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        // log.info("✅ [JWT FILTER] Token válido para usuario: {}", username);
      } catch (Exception e) {
        // Token inválido - limpiar contexto de seguridad
        SecurityContextHolder.clearContext();
        log.warn("[JWT FILTER] Token inválido: {}", e.getMessage());
      }
    }

    // log.info("➡️ [JWT FILTER] Continuando con la cadena de filtros...");
    // Siempre continuar con la cadena de filtros
    filterChain.doFilter(request, response);
    // log.info("✓ [JWT FILTER] {} {} - Completado con status: {}", method, path, response.getStatus());
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // Para fuentes, TODAS las rutas son públicas porque el agregador consulta sin token
    // El filtro JWT solo debe ejecutarse si explícitamente hay un token Bearer
    boolean shouldSkip = true; // Por defecto, saltar el filtro en todas las rutas

    // Log comentado para producción
    // if (shouldSkip) {
    //   log.info("⏭️ [JWT FILTER - FuenteEstatica] Saltando filtro JWT - Todas las rutas son públicas: {}", path);
    // }

    return shouldSkip;
  }
}
