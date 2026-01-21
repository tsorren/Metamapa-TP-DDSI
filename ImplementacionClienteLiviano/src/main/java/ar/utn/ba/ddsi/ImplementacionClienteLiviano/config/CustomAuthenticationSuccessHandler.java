package ar.utn.ba.ddsi.ImplementacionClienteLiviano.config;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {

    HttpSession session = request.getSession(true);

    // ✅ IMPORTANTE: Guardar userId y rol en la sesión para las notificaciones
    /*Object principal = authentication.getPrincipal();

    if (principal instanceof CustomUserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) principal;

      Long userId = userDetails.getId();
      String username = userDetails.getUsername();

      // Obtener el rol principal (el primero en la lista de authorities)
      String rol = userDetails.getAuthorities().stream()
          .findFirst()
          .map(auth -> auth.getAuthority().replace("ROLE_", ""))
          .orElse("CONTRIBUYENTE");

      // Guardar información en la sesión
      session.setAttribute("userId", userId);
      session.setAttribute("rol", rol);
      session.setAttribute("username", username);

      log.info("✅ Sesión configurada exitosamente - userId: {}, rol: {}, username: {}, sessionId: {}",
               userId, rol, username, session.getId());
    } else {
      // ⚠️ Caso excepcional: si el principal no es CustomUserDetails
      log.error("❌ ERROR: Principal no es CustomUserDetails. Tipo: {}. Rechazando autenticación.",
                principal != null ? principal.getClass().getName() : "null");

      // En lugar de usar valores por defecto, redirigir al login con error
      response.sendRedirect("/login?error=invalid_principal");
      return;
    }*/

    // Determinar la URL de redirección
    String targetUrl = determineTargetUrl(request, response, session);

    // Validar y sanitizar la URL de redirección
    targetUrl = validateAndSanitizeTargetUrl(targetUrl);

    // Log comentado para producción
    // log.info("Redirigiendo usuario autenticado a: {}", targetUrl);
    response.sendRedirect(targetUrl);
  }

  /**
   * Determina la URL de destino después de la autenticación exitosa
   */
  private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
    // 1. Primero, intentar obtener la URL guardada por Spring Security
    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
    if (savedRequest != null) {
      String savedUrl = savedRequest.getRedirectUrl();
      // log.debug("URL guardada por Spring Security encontrada: {}", savedUrl);
      return savedUrl;
    }

    // 2. Si no hay savedRequest, buscar el parámetro 'redirectUrl' en la sesión
    Object redirectUrlObj = session.getAttribute("redirectUrl");
    if (redirectUrlObj instanceof String) {
      String redirectUrl = (String) redirectUrlObj;
      session.removeAttribute("redirectUrl"); // Limpiar después de usar
      // log.debug("URL guardada en sesión encontrada: {}", redirectUrl);
      return redirectUrl;
    }

    // 3. URL por defecto
    // log.debug("No hay URL previa, usando página principal por defecto");
    return "/";
  }

  /**
   * Valida y sanitiza la URL de destino para evitar redirecciones no deseadas
   */
  private String validateAndSanitizeTargetUrl(String targetUrl) {
    if (targetUrl == null || targetUrl.trim().isEmpty()) {
      return "/";
    }

    // Eliminar espacios en blanco
    targetUrl = targetUrl.trim();

    // Lista de URLs prohibidas (páginas de autenticación)
    String[] forbiddenPaths = {"/login", "/register", "/logout", "/error", "/access-denied"};

    for (String forbiddenPath : forbiddenPaths) {
      if (targetUrl.contains(forbiddenPath)) {
        log.warn("Intento de redirección a URL prohibida: {}", targetUrl);
        return "/";
      }
    }

    // Evitar redirecciones externas (open redirect vulnerability)
    if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
      log.warn("Intento de redirección externa detectado: {}", targetUrl);
      return "/";
    }

    // Asegurar que la URL comienza con /
    if (!targetUrl.startsWith("/")) {
      targetUrl = "/" + targetUrl;
    }

    return targetUrl;
  }
}
