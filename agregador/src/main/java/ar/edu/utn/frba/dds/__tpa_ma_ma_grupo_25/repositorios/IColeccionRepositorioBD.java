package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColeccionRepositorioBD extends JpaRepository<Coleccion, String> {
  Coleccion findByHandle(String handle);
}
