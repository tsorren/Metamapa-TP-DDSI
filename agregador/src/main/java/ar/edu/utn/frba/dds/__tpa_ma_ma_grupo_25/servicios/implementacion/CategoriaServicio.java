package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CategoriaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
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
    String nombreEntrada = input.getNombre().trim();

    // Paso 1: Buscar coincidencia exacta (por si ya está normalizada)
    Categoria categoriaExacta = categoriaRepositorio.findByNombre(nombreEntrada);
    if (categoriaExacta != null) {
      log.info("<SINCRONIZAR> Categoría '{}' ya existe en Agregador con id {}",
               nombreEntrada, categoriaExacta.getId());
      return categoriaExacta.toDTO();
    }

    // Paso 2: Buscar case-insensitive (puede que exista con otra capitalización)
    List<String> categoriasExistentes = categoriaRepositorio.findAllNames();
    for (String nombreExistente : categoriasExistentes) {
      if (nombreExistente.equalsIgnoreCase(nombreEntrada)) {
        Categoria categoriaExistente = categoriaRepositorio.findByNombre(nombreExistente);
        log.info("<SINCRONIZAR> Categoría '{}' encontrada como '{}' (case-insensitive) en Agregador con id {}",
                 nombreEntrada, nombreExistente, categoriaExistente.getId());
        return categoriaExistente.toDTO();
      }
    }

    // Paso 3: Normalizar (busca similares con Jaro-Winkler)
    Categoria nuevaCategoria = new Categoria(nombreEntrada);
    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(nuevaCategoria);

    log.info("<SINCRONIZAR> Categoría '{}' sincronizada en Agregador con id {}",
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

