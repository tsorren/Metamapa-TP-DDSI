package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxyConfig;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.TipoFuenteProxy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para persistir la configuración de las fuentes proxy.
 */
public interface IFuenteProxyRepositorioBD extends JpaRepository<FuenteProxyConfig, Long> {
    
    /**
     * Busca todas las fuentes proxy activas
     */
    List<FuenteProxyConfig> findByActivaTrue();
    
    /**
     * Busca fuentes por tipo
     */
    List<FuenteProxyConfig> findByTipo(TipoFuenteProxy tipo);
    
    /**
     * Busca fuentes activas por tipo
     */
    List<FuenteProxyConfig> findByTipoAndActivaTrue(TipoFuenteProxy tipo);
    
    /**
     * Verifica si existe una fuente con la URL dada
     */
    boolean existsByBaseUrl(String baseUrl);
    
    /**
     * Busca una fuente por URL
     */
    Optional<FuenteProxyConfig> findByBaseUrl(String baseUrl);
    
    /**
     * Cuenta las fuentes activas
     */
    long countByActivaTrue();
}

