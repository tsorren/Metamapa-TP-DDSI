package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FiltroInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FiltroOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.Filtro;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFiltroServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/filtros")
// TODO: Crear Output DTOs
public class FiltroControlador {
  private final IFiltroServicio filtroServicio;

  public FiltroControlador(IFiltroServicio filtroServicio) {
    this.filtroServicio = filtroServicio;
  }

  @GetMapping()
  public List<Filtro> encontrarTodos() {
    return filtroServicio.encontrarTodos();
  }

  @PostMapping()
  public FiltroOutputDTO crear(@RequestBody FiltroInputDTO filtroInputDTO) {
    return filtroServicio.crear(filtroInputDTO);
  }
}
