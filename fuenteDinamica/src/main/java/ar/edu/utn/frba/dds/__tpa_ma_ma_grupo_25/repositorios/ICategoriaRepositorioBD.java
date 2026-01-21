package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ICategoriaRepositorioBD extends JpaRepository<Categoria, Long> {
  @Query("SELECT c.nombre FROM Categoria c")
  List<String> findAllNames();

  Categoria findByNombre(String nombre);
}
