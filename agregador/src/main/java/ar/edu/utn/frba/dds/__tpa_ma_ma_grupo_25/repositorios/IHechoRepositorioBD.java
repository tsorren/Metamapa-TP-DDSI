package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHechoRepositorioBD extends JpaRepository<Hecho, Long> {
  List<Hecho> findByTitulo(String titulo);

  // Buscar por la fuente completa CON JOIN FETCH para cargar TODAS las relaciones lazy
  // Esto evita LazyInitializationException al llamar a toDTO()
  @Query("SELECT DISTINCT h FROM Hecho h " +
          "LEFT JOIN FETCH h.fuente " +
          "LEFT JOIN FETCH h.categoria " +
          "LEFT JOIN FETCH h.contenido " +
          "LEFT JOIN FETCH h.etiquetas " +
          "WHERE h.fuente = :fuente")
  List<Hecho> findByFuenteWithFetch(@Param("fuente") Fuente fuente);

  // Buscar hechos por IDs con fetch de consensos para procesamiento asíncrono
  @Query("SELECT DISTINCT h FROM Hecho h " +
          "LEFT JOIN FETCH h.consensos " +
          "WHERE h.id IN :ids")
  List<Hecho> findByIdsWithConsensos(@Param("ids") List<Long> ids);

  Hecho findHechoByFuenteIdAndIdExterno(Long fuenteId, Long idExterno);



}
