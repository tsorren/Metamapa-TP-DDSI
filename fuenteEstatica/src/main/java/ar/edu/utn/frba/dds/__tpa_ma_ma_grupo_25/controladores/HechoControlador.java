package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocultar")
public class HechoControlador {
  private final IHechoServicio hechoServicio;

  public HechoControlador(IHechoServicio hechoServicio) {
    this.hechoServicio = hechoServicio;
  }

  @PatchMapping("/{idHecho}")
  public ResponseEntity<Void> ocultarHecho(@PathVariable("idHecho") Long idHecho) {
    hechoServicio.ocultarHecho(idHecho);
    return ResponseEntity.ok().build();
  }
}
