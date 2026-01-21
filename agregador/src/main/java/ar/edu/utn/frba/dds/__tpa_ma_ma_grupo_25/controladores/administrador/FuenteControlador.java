package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/fuentes")
// TODO: Crear Output DTOs
public class FuenteControlador {

  private final IFuenteServicio fuenteServicio;

  public FuenteControlador(IFuenteServicio fuenteServicio) {
    this.fuenteServicio = fuenteServicio;
  }

  @GetMapping()
  public List<FuenteOutputDTO> encontrarTodas() {
    return fuenteServicio.encontrarTodasDTO();
  }

  @GetMapping("/{id}")
  public List<Hecho> hechosDeFuente(@PathVariable("id") Long id) {
    return fuenteServicio.hechosDeFuente(id);
  }

  @GetMapping("/dinamica/{id}")
  public HechoOutputDTO hechoDeFuenteDinamica(@PathVariable("id") Long id) {
    return fuenteServicio.hechoDeFuenteDinamica(id);
  }
}
