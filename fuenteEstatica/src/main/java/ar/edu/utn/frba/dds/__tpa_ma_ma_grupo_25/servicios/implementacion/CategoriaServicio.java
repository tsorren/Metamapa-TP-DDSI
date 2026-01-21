package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CategoriaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICategoriaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoriaServicio implements ICategoriaServicio {

  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final NormalizadorCategoria normalizadorCategoria;

  public CategoriaServicio(ICategoriaRepositorioBD categoriaRepositorio,
                           NormalizadorCategoria normalizadorCategoria) {
    this.categoriaRepositorio = categoriaRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
  }

  @Override
  @Transactional
  public CategoriaOutputDTO sincronizar(CategoriaInputDTO input) {
    // Buscar si ya existe una categoría con ese nombre o similar
    Categoria categoriaExistente = categoriaRepositorio.findByNombre(input.getNombre());

    if (categoriaExistente != null) {
      log.info("<SINCRONIZAR> Categoría '{}' ya existe en FuenteEstatica con id {}",
               input.getNombre(), categoriaExistente.getId());
      return categoriaExistente.toDTO();
    }

    // Normalizar la categoría para verificar si hay una similar
    Categoria nuevaCategoria = new Categoria(input.getNombre());
    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(nuevaCategoria);

    log.info("<SINCRONIZAR> Categoría '{}' sincronizada en FuenteEstatica con id {}",
             categoriaNormalizada.getNombre(), categoriaNormalizada.getId());

    return categoriaNormalizada.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public CategoriaOutputDTO findByNombre(String nombre) {
    Categoria categoria = categoriaRepositorio.findByNombre(nombre);
    return categoria != null ? categoria.toDTO() : null;
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoriaOutputDTO> findAll() {
    return categoriaRepositorio.findAll().stream()
            .map(Categoria::toDTO)
            .collect(Collectors.toList());
  }
}

