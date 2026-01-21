package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CriterioDePertenenciaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CriterioDePertenenciaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Accion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICriterioDePertenenciaServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Crear Output DTOs
@RestController
@RequestMapping("/admin/criteriosDePertenencia")
public class CriterioDePertenenciaControlador {
  private final ICriterioDePertenenciaServicio criterioServicio;

  public CriterioDePertenenciaControlador(ICriterioDePertenenciaServicio criterioServicio) {
    this.criterioServicio = criterioServicio;
  }

  @GetMapping()
  public List<CriterioDePertenenciaOutputDTO> encontrarTodos() {
    return criterioServicio.encontrarTodos();
  }

  @GetMapping("/{id}")
  public CriterioDePertenenciaOutputDTO encontrarPorId(@PathVariable Long id) {
    return criterioServicio.encontrarPorId(id);
  }

  @PostMapping()
  public CriterioDePertenenciaOutputDTO crear() {
    return criterioServicio.crear();
  }

  @PatchMapping("/{id}")
  public CriterioDePertenenciaOutputDTO modificarFiltro(@PathVariable Long id, @RequestBody CriterioDePertenenciaInputDTO input) {
    if (input.getAccion().equals(Accion.AGREGAR)) {
      return criterioServicio.agregarFiltro(id, input.getIdFiltro());
    } else if (input.getAccion().equals(Accion.QUITAR)) {
      return criterioServicio.quitarFiltro(id, input.getIdFiltro());
    }
    return null;
  }

}
