package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas.HechoProcesado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HechoProcesadoRepositorioBD extends JpaRepository<HechoProcesado, Long> {

  @Query("SELECT h FROM HechoProcesado h WHERE " +
          "LOWER(h.categoria) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
          "LOWER(h.provincia) LIKE LOWER(CONCAT('%', :texto, '%'))")
  List<HechoProcesado> busquedaLibre(@Param("texto") String texto);
}
