package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contribuyentes")
public class ContribuyenteControlador {
  private final IContribuyenteServicio contribuyenteServicio;

  public ContribuyenteControlador(IContribuyenteServicio contribuyenteServicio) {
    this.contribuyenteServicio = contribuyenteServicio;
  }

  @PostMapping()
  public ContribuyenteOutputDTO crear(@RequestBody ContribuyenteInputDTO inputDTO) {
    return contribuyenteServicio.crear(inputDTO.getId(), inputDTO.getNombre(), inputDTO.getApellido(), inputDTO.getEdad());
  }

  @GetMapping("/{id}")
  public ContribuyenteOutputDTO encontrar(@PathVariable Long id) {
    return contribuyenteServicio.findById(id);
  }

  @GetMapping("/{id}/hechos")
  public List<HechoOutputDTO> hechosDeContribuyente(@PathVariable Long id) {
    return contribuyenteServicio.hechosDeContribuyente(id);
  }

}
