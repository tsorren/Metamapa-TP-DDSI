package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContribuyenteRepositorioBD extends JpaRepository<Contribuyente, Long> {
  Long id(Long id);
}
