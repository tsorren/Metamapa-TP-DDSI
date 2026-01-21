package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para el módulo de observabilidad
 */
@Configuration
public class ObservabilidadConfig {

    /**
     * Bean de RestTemplate para hacer llamadas HTTP a otros microservicios
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

