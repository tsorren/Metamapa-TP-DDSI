package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("=== 🔒 CONFIGURANDO SECURITY CHAIN (FuenteDinamica) ===");

    http
        .cors(cors -> {
          log.info("✓ Habilitando CORS");
          cors.configurationSource(corsConfigurationSource());
        })
        .csrf(csrf -> {
          log.info("✓ Deshabilitando CSRF");
          csrf.disable();
        })
        .httpBasic(httpBasic -> {
          log.info("✓ Deshabilitando HTTP Basic Auth");
          httpBasic.disable();
        })
        .formLogin(formLogin -> {
          log.info("✓ Deshabilitando Form Login");
          formLogin.disable();
        })
        .sessionManagement(session -> {
          log.info("✓ Configurando sesiones como STATELESS");
          session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        })
        .authorizeHttpRequests(auth -> {
            log.info("✓ Configurando reglas de autorización:");
            log.info("  - /** → PÚBLICO (todas las rutas)");
            auth.anyRequest().permitAll();
        });
        // ✅ NO agregamos el filtro JWT - todas las rutas son públicas

    log.info("=== ✅ SECURITY CHAIN CONFIGURADO (sin JWT) ===");
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    log.info("🌐 Configurando CORS:");

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    log.info("  ✓ Allowed Origin Patterns: * (todos los orígenes)");

    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    log.info("  ✓ Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS");

    configuration.setAllowedHeaders(Arrays.asList("*"));
    log.info("  ✓ Allowed Headers: * (todos los headers)");

    configuration.setAllowCredentials(true);
    log.info("  ✓ Allow Credentials: true");

    configuration.setExposedHeaders(Arrays.asList(
        "Authorization",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "Content-Type"
    ));
    log.info("  ✓ Exposed Headers: Authorization, Access-Control-Allow-Origin, Access-Control-Allow-Credentials, Content-Type");

    configuration.setMaxAge(3600L);
    log.info("  ✓ Max Age: 3600s");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    log.info("🌐 CORS configurado para todos los paths (/**) con soporte HTTPS");
    return source;
  }
}