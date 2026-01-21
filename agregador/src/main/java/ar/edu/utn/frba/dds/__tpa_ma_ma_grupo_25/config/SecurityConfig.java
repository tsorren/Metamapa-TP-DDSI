package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.middlewares.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.debug("Configurando Security Chain");

    http
        // CORS primero
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        // Deshabilitar CSRF
        .csrf(csrf -> csrf.disable())
        // Configurar reglas de autorización
        .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/usuarios/auth", "/usuarios/auth/**").permitAll();
            auth.requestMatchers("/colecciones", "/colecciones/**").permitAll();
            auth.requestMatchers("/hechos", "/hechos/**").permitAll();
            auth.requestMatchers("/categorias", "/categorias/**").permitAll();
            auth.requestMatchers("/fuentes", "/fuentes/**").permitAll();
            auth.requestMatchers("/actuator", "/actuator/**").permitAll();
            auth.requestMatchers("/graphql", "/graphql/**").permitAll();
            auth.requestMatchers("/error", "/error/**").permitAll();
            auth.requestMatchers("/favicon.ico").permitAll();
            auth.requestMatchers("/auth/roles-permisos").authenticated();
            auth.requestMatchers("/admin/**").authenticated();
            auth.anyRequest().permitAll();
        })
        // Sesiones STATELESS
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Habilitar acceso anónimo explícitamente
        .anonymous(anonymous -> {})
        // Configurar manejo de excepciones - SOLO para rutas que requieren autenticación
        .exceptionHandling(ex -> {
          ex.authenticationEntryPoint((request, response, authException) -> {
            String path = request.getRequestURI();
            log.warn("Acceso no autorizado a: {} - Error: {}", path, authException.getMessage());
            // Solo devolver 401 si es realmente un problema de autenticación en ruta protegida
            if (path.startsWith("/admin") || path.equals("/auth/roles-permisos")) {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json");
              response.getWriter().write("{\"error\":\"No autorizado\",\"path\":\"" + path + "\"}");
            } else {
              // Para rutas públicas, dejar que el error fluya normalmente
              response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno");
            }
          });
        })
        // Agregar filtro JWT
        .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    log.info("Security Chain configurado correctamente");
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "Content-Type"
    ));
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    log.debug("CORS configurado para todos los paths");
    return source;
  }
}

