package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContenidoRepositorioBD extends JpaRepository<Multimedia, Long> {
}
