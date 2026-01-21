package ar.utn.ba.ddsi.ImplementacionClienteLiviano.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Validador de configuración para el Cliente Liviano
 * Verifica que las URLs de los servicios externos estén configuradas correctamente
 */
@Component
public class ConfiguracionValidator {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracionValidator.class);

    @Value("${agregador.url}")
    private String agregadorUrl;

    @Value("${agregador.graphql.url}")
    private String agregadorGraphqlUrl;

    @PostConstruct
    public void validarConfiguracion() {
        logger.info("=== VALIDANDO CONFIGURACIÓN DEL CLIENTE LIVIANO ===");

        // Validar URL del agregador
        if (agregadorUrl == null || agregadorUrl.trim().isEmpty()) {
            logger.error("❌ ERROR: agregador.url no está configurada");
            throw new IllegalStateException("La URL del agregador debe estar configurada");
        }
        logger.info("✅ agregador.url configurada: {}", agregadorUrl);

        // Validar URL GraphQL del agregador
        if (agregadorGraphqlUrl == null || agregadorGraphqlUrl.trim().isEmpty()) {
            logger.error("❌ ERROR: agregador.graphql.url no está configurada");
            throw new IllegalStateException("La URL GraphQL del agregador debe estar configurada");
        }
        logger.info("✅ agregador.graphql.url configurada: {}", agregadorGraphqlUrl);

        // Informar sobre el protocolo (el AgregadorClient agregará https:// automáticamente si es necesario)
        if (!agregadorUrl.startsWith("http://") && !agregadorUrl.startsWith("https://")) {
            logger.info("ℹ️ INFO: agregador.url sin protocolo - se agregará https:// automáticamente");
        }

        if (!agregadorGraphqlUrl.startsWith("http://") && !agregadorGraphqlUrl.startsWith("https://")) {
            logger.info("ℹ️ INFO: agregador.graphql.url sin protocolo - se agregará https:// automáticamente");
        }

        logger.info("=== CONFIGURACIÓN VALIDADA CORRECTAMENTE ===");
    }
}

