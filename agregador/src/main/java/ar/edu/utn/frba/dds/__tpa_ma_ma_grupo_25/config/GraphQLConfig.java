package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Configuración de GraphQL para registrar scalars personalizados
 */
@Configuration
public class GraphQLConfig {

    /**
     * Registra los scalars personalizados necesarios para el schema
     * - Long: para identificadores y contadores grandes
     * - DateTime: para fechas con hora
     * - Date: para fechas sin hora
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.Date);
    }
}

