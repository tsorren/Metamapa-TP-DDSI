package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.EtiquetaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EtiquetaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEtiquetaServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/etiquetas")
public class EtiquetaControlador {

  private final IEtiquetaServicio etiquetaServicio;

  public EtiquetaControlador(IEtiquetaServicio etiquetaServicio) {
    this.etiquetaServicio = etiquetaServicio;
  }

  @PostMapping()
  public EtiquetaOutputDTO crear(@RequestBody EtiquetaInputDTO inputDTO) {
    return etiquetaServicio.crear(inputDTO.getNombre());
  }

  @GetMapping()
  public List<EtiquetaOutputDTO> encontrarTodas() {
    return etiquetaServicio.encontrarTodas();
  }

  @GetMapping("/{id}")
  public EtiquetaOutputDTO encontrarPorId(@PathVariable("id") long id) {
    return etiquetaServicio.encontrarPorId(id);
  }
}
