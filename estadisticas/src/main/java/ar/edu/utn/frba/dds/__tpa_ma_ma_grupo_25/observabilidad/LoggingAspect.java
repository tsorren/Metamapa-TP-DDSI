package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.observabilidad;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger("http.metrics");

    @Around("execution(* ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        String correlationId = UUID.randomUUID().toString();
        String methodName = joinPoint.getSignature().toShortString();

        MDC.put("correlationId", correlationId);
        MDC.put("requestId", requestId);
        MDC.put("method", methodName);

        long startTime = System.currentTimeMillis();

        try {
            // Log de inicio comentado para producción (spam)
            // logger.info("Starting execution of {}", methodName);
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            // Log de éxito comentado para producción (spam)
            // logger.info("Successfully completed execution of {}", methodName);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            logger.error("Error executing {}: {}", methodName, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}


