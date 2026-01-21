package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = getClientId(request);
        boolean allowed = rateLimitService.tryConsume(clientId);

        if (!allowed) {
            logger.warn("Rate limit exceeded for client: {}", clientId);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = String.format(
                "{\"error\":\"Too Many Requests\",\"message\":\"Has excedido el limite de solicitudes. Intenta nuevamente mas tarde.\",\"clientId\":\"%s\"}",
                clientId
            );

            response.getWriter().write(jsonResponse);
            return false;
        }
        // Agregar headers informativos
        long availableTokens = rateLimitService.getAvailableTokens(clientId);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));
        response.setHeader("X-RateLimit-Limit", "500");

        logger.debug("Request allowed for client: {} (remaining: {})", clientId, availableTokens);
        return true;
    }

    private String getClientId(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }
}

