package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.ratelimit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Servicio de Rate Limiting que limita la cantidad de solicitudes por cliente.
 * Utiliza Bucket4j para implementar el algoritmo Token Bucket.
 */
@Service
public class RateLimitService {

    private final LoadingCache<String, Bucket> cache;

    public RateLimitService() {
        // Configurar cache con Caffeine
        this.cache = Caffeine.newBuilder()
                .maximumSize(100_000) // Máximo 100,000 clientes en cache
                .expireAfterAccess(Duration.ofMinutes(10)) // Expira después de 10 minutos de inactividad
                .build(this::createNewBucket);
    }

    /**
     * Verifica si una solicitud desde el cliente está permitida.
     *
     * @param clientId Identificador único del cliente (IP, userId, etc.)
     * @return true si la solicitud está permitida, false si se excedió el límite
     */
    public boolean tryConsume(String clientId) {
        Bucket bucket = cache.get(clientId);
        return bucket != null && bucket.tryConsume(1);
    }

    /**
     * Obtiene el número de tokens disponibles para un cliente.
     *
     * @param clientId Identificador único del cliente
     * @return Número de tokens disponibles
     */
    public long getAvailableTokens(String clientId) {
        Bucket bucket = cache.get(clientId);
        return bucket != null ? bucket.getAvailableTokens() : 0;
    }

    /**
     * Crea un nuevo bucket con la configuración de rate limiting.
     * Configuración actual: 500 solicitudes por minuto
     *
     * @param clientId Identificador del cliente
     * @return Bucket configurado
     */
    private Bucket createNewBucket(String clientId) {
        // Configuración: 500 solicitudes por minuto
        Bandwidth limit = Bandwidth.classic(500, Refill.intervally(500, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Crea un bucket con configuración personalizada.
     *
     * @param capacity Capacidad máxima del bucket
     * @param refillTokens Tokens a reponer
     * @param refillDuration Duración para reponer tokens
     * @return Bucket configurado
     */
    public Bucket createCustomBucket(long capacity, long refillTokens, Duration refillDuration) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.intervally(refillTokens, refillDuration));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

