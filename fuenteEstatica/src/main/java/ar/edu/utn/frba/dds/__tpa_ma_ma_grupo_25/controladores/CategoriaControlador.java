package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CategoriaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICategoriaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categorias")
public class CategoriaControlador {

  private final ICategoriaServicio categoriaServicio;

  public CategoriaControlador(ICategoriaServicio categoriaServicio) {
    this.categoriaServicio = categoriaServicio;
  }

  @PostMapping("/sincronizar")
  public CategoriaOutputDTO sincronizar(@RequestBody CategoriaInputDTO input) {
    log.info("<SINCRONIZAR> Recibida categoría '{}' para sincronizar en FuenteEstatica", input.getNombre());
    return categoriaServicio.sincronizar(input);
  }

  @GetMapping()
  public List<CategoriaOutputDTO> findAll() {
    return categoriaServicio.findAll();
  }

  @GetMapping("/buscar")
  public CategoriaOutputDTO findByNombre(@RequestParam String nombre) {
    return categoriaServicio.findByNombre(nombre);
  }
}

