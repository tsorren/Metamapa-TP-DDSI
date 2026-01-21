package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para verificar el estado del Rate Limiting.
 */
@RestController
@RequestMapping("/api/rate-limit")
public class RateLimitController {

    @Autowired
    private RateLimitService rateLimitService;

    /**
     * Endpoint de prueba para verificar el rate limiting.
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRateLimit(HttpServletRequest request) {
        String clientId = getClientId(request);
        long availableTokens = rateLimitService.getAvailableTokens(clientId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Solicitud exitosa");
        response.put("clientId", clientId);
        response.put("remainingRequests", availableTokens);
        response.put("limit", 100);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar el estado del rate limit sin consumir tokens.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getRateLimitStatus(HttpServletRequest request) {
        String clientId = getClientId(request);
        long availableTokens = rateLimitService.getAvailableTokens(clientId);

        Map<String, Object> response = new HashMap<>();
        response.put("clientId", clientId);
        response.put("remainingRequests", availableTokens);
        response.put("limit", 500);
        response.put("timeWindow", "1 minute");

        return ResponseEntity.ok(response);
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

