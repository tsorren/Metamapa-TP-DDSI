package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IModuloFuenteRepositorioBD extends JpaRepository<ModuloFuente, Long> {
  ModuloFuente findByTipo(TipoFuente tipoFuente);
}
