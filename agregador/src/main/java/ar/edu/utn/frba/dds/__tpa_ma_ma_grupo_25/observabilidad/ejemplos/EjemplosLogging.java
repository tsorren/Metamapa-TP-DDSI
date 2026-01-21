package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad.ejemplos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Ejemplos de uso del sistema de logging estructurado
 */
@Component
public class EjemplosLogging {

    private static final Logger logger = LoggerFactory.getLogger(EjemplosLogging.class);
    private static final Logger httpLogger = LoggerFactory.getLogger("http.metrics");
    private static final Logger businessLogger = LoggerFactory.getLogger("business.error");

    // Ejemplo 1: Niveles básicos
    public void ejemploNivelesBasicos() {
        logger.debug("Información de debugging");
        logger.info("Usuario autenticado exitosamente");
        logger.warn("Tiempo de respuesta alto detectado");
        logger.error("Error al conectar con BD");
    }

    // Ejemplo 2: Con contexto usando placeholders
    public void ejemploConContexto() {
        String userId = "user123";
        int intentos = 3;
        logger.info("Usuario {} inició sesión después de {} intentos", userId, intentos);
    }

    // Ejemplo 3: Excepciones con stack trace
    public void ejemploExcepciones() {
        try {
            throw new RuntimeException("Error simulado");
        } catch (Exception e) {
            logger.error("Error al procesar solicitud", e);
            logger.error("Error del usuario {}", "user123", e);
        }
    }

    // Ejemplo 4: MDC para contexto de request
    public void ejemploMDC() {
        try {
            MDC.put("userId", "user123");
            MDC.put("operationType", "CREATE_HECHO");
            logger.info("Procesando operación");
        } finally {
            MDC.clear();
        }
    }

    // Ejemplo 5: Métricas HTTP
    public void ejemploMetricasHTTP() {
        long startTime = System.currentTimeMillis();
        try {
            MDC.put("endpoint", "/api/hechos");
            MDC.put("httpMethod", "POST");
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            MDC.put("httpStatus", "200");
            httpLogger.info("HTTP Request completed successfully");
        } finally {
            MDC.clear();
        }
    }

    // Ejemplo 6: Errores de negocio
    public void ejemploErrorNegocio() {
        businessLogger.warn("Hecho {} rechazado: {}", "hecho-123", "Contenido spam");
    }

    // Ejemplo 7: Log condicional
    public void ejemploLogCondicional() {
        if (logger.isDebugEnabled()) {
            String datosComplejos = "datos...";
            logger.debug("Datos generados: {}", datosComplejos);
        }
    }

    // Ejemplo 8: Auditoría
    public void ejemploAuditoria() {
        logger.info("AUDIT: Usuario {} ejecutó {} sobre {}", "admin", "DELETE", "user456");
    }

    // Ejemplo 9: Operación larga
    public void ejemploOperacionLarga() {
        String opId = "import-123";
        logger.info("Iniciando importación, opId: {}", opId);
        long start = System.currentTimeMillis();
        try {
            // operación...
            long duration = System.currentTimeMillis() - start;
            logger.info("Importación completada, opId: {}, duration: {}ms", opId, duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            logger.error("Importación falló, opId: {}, duration: {}ms", opId, duration, e);
        }
    }

    // Ejemplo 10: Log estructurado
    public void ejemploEstructurado() {
        MDC.put("eventType", "USER_REGISTRATION");
        MDC.put("userId", "user789");
        MDC.put("email", "user@example.com");
        logger.info("Nuevo usuario registrado");
        MDC.clear();
    }
}

