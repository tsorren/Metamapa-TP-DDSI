package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para registrar el interceptor de Rate Limiting y CORS.
 * Aplica el rate limiting a todos los endpoints excepto algunos excluidos.
 */
@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuración CORS para permitir peticiones del cliente liviano
        registry.addMapping("/**")
                .allowedOrigins("*") // Permitir todos los orígenes (cambiar en producción)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**") // Aplica a todos los endpoints
                .excludePathPatterns(
                        "/actuator/**",      // Excluir endpoints de actuator
                        "/graphql",          // Excluir GraphQL endpoint
                        "/graphiql/**",      // Excluir GraphQL playground
                        "/usuarios/auth/**", // Excluir autenticación
                        "/usuarios/login",   // Excluir login
                        "/usuarios/register",// Excluir registro
                        "/error",            // Excluir página de error
                        "/favicon.ico"       // Excluir favicon
                );
    }
}
