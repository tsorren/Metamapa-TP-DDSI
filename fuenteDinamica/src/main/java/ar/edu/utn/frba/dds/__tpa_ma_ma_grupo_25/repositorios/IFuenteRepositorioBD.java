package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteDinamica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFuenteRepositorioBD extends JpaRepository<FuenteDinamica, Long> {
}
