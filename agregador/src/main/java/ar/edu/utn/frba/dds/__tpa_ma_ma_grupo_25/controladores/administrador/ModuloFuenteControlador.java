package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ModuloFuenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ModuloFuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/modulo")
public class ModuloFuenteControlador {
  private final IModuloFuenteServicio moduloFuenteServicio;

  public ModuloFuenteControlador(IModuloFuenteServicio moduloFuenteServicio) {
    this.moduloFuenteServicio = moduloFuenteServicio;
  }

  @PostMapping()
  public ResponseEntity<?> crear(@RequestBody ModuloFuenteInputDTO input) {
    try {
      ModuloFuenteOutputDTO modulo = moduloFuenteServicio.crear(input.getTipo(), input.getUrl());
      return ResponseEntity.ok(modulo);
    } catch (Exception e) {
      Map<String, String> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
  }


  @GetMapping()
  public List<ModuloFuenteOutputDTO> consultarFuentes() { // TODO: Crear DTOs
    return moduloFuenteServicio.encontrarTodas();
  }

}
