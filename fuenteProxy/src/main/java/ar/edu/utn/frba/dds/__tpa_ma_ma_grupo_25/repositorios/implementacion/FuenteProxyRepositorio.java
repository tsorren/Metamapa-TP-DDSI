package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteDesastres;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteMetamapa;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxy;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxyConfig;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.TipoFuenteProxy;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteProxyRepositorio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteProxyRepositorioBD;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio que combina persistencia en BD con caché en memoria.
 * La configuración se persiste en la BD, pero los objetos FuenteProxy
 * (con sus WebClients) se mantienen en memoria para su uso.
 */
@Slf4j
@Repository
public class FuenteProxyRepositorio implements IFuenteProxyRepositorio {

    private final IFuenteProxyRepositorioBD fuenteProxyRepositorioBD;
    private final WebClient.Builder webClientBuilder;

    // Caché en memoria de FuenteProxy instanciados (id -> FuenteProxy)
    private final Map<Long, FuenteProxy> fuentesCache;

    public FuenteProxyRepositorio(IFuenteProxyRepositorioBD fuenteProxyRepositorioBD,
                                   WebClient.Builder webClientBuilder) {
        this.fuenteProxyRepositorioBD = fuenteProxyRepositorioBD;
        this.webClientBuilder = webClientBuilder;
        this.fuentesCache = new ConcurrentHashMap<>();
    }

    /**
     * Al iniciar la aplicación, carga las fuentes desde la BD al caché
     */
    @PostConstruct
    public void inicializarCache() {
        log.info("Inicializando caché de fuentes proxy desde la base de datos...");
        List<FuenteProxyConfig> configs = fuenteProxyRepositorioBD.findByActivaTrue();
        for (FuenteProxyConfig config : configs) {
            try {
                FuenteProxy fuente = instanciarFuente(config);
                fuentesCache.put(config.getId(), fuente);
                log.info("Fuente cargada al caché: id={}, tipo={}, url={}",
                        config.getId(), config.getTipo(), config.getBaseUrl());
            } catch (Exception e) {
                log.error("Error al cargar fuente id={}: {}", config.getId(), e.getMessage());
            }
        }
        log.info("Caché inicializado con {} fuentes", fuentesCache.size());
    }

    /**
     * Instancia una FuenteProxy a partir de su configuración
     */
    private FuenteProxy instanciarFuente(FuenteProxyConfig config) {
        FuenteProxy fuente;
        if (config.getTipo() == TipoFuenteProxy.DESASTRESAPI) {
            fuente = new FuenteDesastres(
                    webClientBuilder.baseUrl(config.getBaseUrl()).build(),
                    config.getBaseUrl(),
                    config.getEmail(),
                    config.getPassword()
            );
        } else {
            fuente = new FuenteMetamapa(
                    webClientBuilder.baseUrl(config.getBaseUrl()).build(),
                    config.getBaseUrl()
            );
        }
        fuente.setIdentificador(config.getId());
        return fuente;
    }

    @Override
    public void guardar(FuenteProxy fuente) {
        // Crear la configuración a persistir
        FuenteProxyConfig config;

        if (fuente instanceof FuenteDesastres fuenteDesastres) {
            config = FuenteProxyConfig.crearDesastres(
                    fuente.getBaseUrl(),
                    fuenteDesastres.getEmail(),
                    fuenteDesastres.getPassword(),
                    null
            );
        } else if (fuente instanceof FuenteMetamapa) {
            config = FuenteProxyConfig.crearMetamapa(fuente.getBaseUrl(), null);
        } else {
            throw new IllegalArgumentException("Tipo de fuente no soportado: " + fuente.getClass().getName());
        }

        // Si ya tiene ID, es una actualización
        if (fuente.getIdentificador() != null) {
            config.setId(fuente.getIdentificador());
        }

        // Persistir en BD
        config = fuenteProxyRepositorioBD.save(config);

        // Actualizar el ID de la fuente
        fuente.setIdentificador(config.getId());

        // Agregar al caché
        fuentesCache.put(config.getId(), fuente);

        log.info("Fuente guardada: id={}, tipo={}, url={}",
                config.getId(), config.getTipo(), config.getBaseUrl());
    }

    @Override
    public List<FuenteProxy> encontrarTodas() {
        return new ArrayList<>(fuentesCache.values());
    }

    @Override
    public FuenteProxy encontrarPorId(Long identificador) {
        // Primero buscar en caché
        FuenteProxy fuente = fuentesCache.get(identificador);
        if (fuente != null) {
            return fuente;
        }

        // Si no está en caché, buscar en BD y cargar
        return fuenteProxyRepositorioBD.findById(identificador)
                .filter(FuenteProxyConfig::getActiva)
                .map(config -> {
                    FuenteProxy nuevaFuente = instanciarFuente(config);
                    fuentesCache.put(config.getId(), nuevaFuente);
                    return nuevaFuente;
                })
                .orElse(null);
    }
}


