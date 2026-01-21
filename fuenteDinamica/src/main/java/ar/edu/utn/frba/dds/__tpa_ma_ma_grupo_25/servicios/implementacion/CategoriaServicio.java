package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICategoriaServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaServicio implements ICategoriaServicio {
  private final ICategoriaRepositorioBD categoriaRepositorio;

  public CategoriaServicio(ICategoriaRepositorioBD categoriaRepositorio) {
    this.categoriaRepositorio = categoriaRepositorio;
  }

  @Override
  @Transactional(readOnly = true)
  public Categoria findById(Long id) {
    return categoriaRepositorio.findById(id).orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public Categoria findByNombre(String categoria) {
    return categoriaRepositorio.findByNombre(categoria);
  }

  @Override
  @Transactional
  public Categoria save(Categoria categoria) {
    return categoriaRepositorio.save(categoria);
  }

  @Override
  @Transactional
  public Categoria crear(String categoria) {
    return this.save(new Categoria(categoria));
  }
}
