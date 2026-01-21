package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IFuenteRepositorioBD extends JpaRepository<Fuente, Long> {
  Optional<Fuente> findByModulo(ModuloFuente modulo);

  Optional<Fuente> findByModuloAndIdentificadorExterno(ModuloFuente modulo, Long identificadorExterno);
}
