package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones.UsuarioExistenteException;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Crear Output DTOs
@RestController
@RequestMapping("/contribuyentes")
public class ContribuyenteControlador {

  private final IContribuyenteServicio contribuyenteServicio;

  public ContribuyenteControlador(IContribuyenteServicio contribuyenteServicio) {
    this.contribuyenteServicio = contribuyenteServicio;
  }

  @PostMapping()
  public ContribuyenteOutputDTO crear(@RequestBody ContribuyenteInputDTO inputDTO) {
    return contribuyenteServicio.crear(inputDTO);
  }
}
