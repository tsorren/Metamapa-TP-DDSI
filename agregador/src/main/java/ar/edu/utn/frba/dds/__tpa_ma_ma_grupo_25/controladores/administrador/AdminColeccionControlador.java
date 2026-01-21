package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ColeccionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ConsultaColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.ConsensoPlanificador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IColeccionServicio;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/colecciones")
public class AdminColeccionControlador {
  private final IColeccionServicio coleccionServicio;
  private final ConsensoPlanificador consensoPlanificador;

  public AdminColeccionControlador(IColeccionServicio coleccionServicio, ConsensoPlanificador consensoPlanificador) {

    this.coleccionServicio = coleccionServicio;
    this.consensoPlanificador = consensoPlanificador;
  }

  @PostMapping
  public ColeccionOutputDTO crear(@RequestBody ColeccionInputDTO coleccionInputDTO) {
    return this.coleccionServicio.crear(coleccionInputDTO);
  }

  @PatchMapping("/{handle}")
  public ColeccionOutputDTO modificar(@PathVariable("handle") String handle, @RequestBody ColeccionInputDTO coleccionInputDTO) {
    return this.coleccionServicio.modificar(handle, coleccionInputDTO);
  }

  @PatchMapping("/{handle}/quitar/{idFuente}")
  public ColeccionOutputDTO modificarFuente(
          @PathVariable("idFuente") Long idFuente,
          @PathVariable("handle") String handle,
          @RequestParam("accion") String accion) //"agregar", "quitar"
  {
    if (accion.equals("agregar")) {
      return this.coleccionServicio.agregarFuente(handle, idFuente);
    } else if (accion.equals("quitar")) {
      return this.coleccionServicio.quitarFuente(handle, idFuente);
    }
    return null;
  }

  @DeleteMapping("/{handle}")
  public void eliminar(@PathVariable("handle") String handle) {
    this.coleccionServicio.eliminar(handle);
  }

  @GetMapping("/{handleColeccion}")
  public ConsultaColeccionOutputDTO consultarColeccion(
          @PathVariable("handleColeccion") String handle,
          @RequestParam(value = "navegacion_curada", required = false) Boolean navegacionCurada,
          @RequestParam(value = "categoria", required = false) String categoria,
          @RequestParam(value = "tipo_origen", required = false) String tipoOrigen,
          @RequestParam(value = "fecha_reporte_desde", required = false) LocalDateTime fechaReporteDesde,
          @RequestParam(value = "fecha_reporte_hasta", required = false) LocalDateTime fechaReporteHasta,
          @RequestParam(value = "fecha_acontecimiento_desde", required = false) LocalDate fechaAcontecimientoDesde,
          @RequestParam(value = "fecha_acontecimiento_hasta", required = false) LocalDate fechaAcontecimientoHasta,
          @RequestParam(value = "latitud", required = false) Double latitud,
          @RequestParam(value = "longitud", required = false) Double longitud,
          @RequestParam(value = "radio", required = false) Double radio
  ) {
    NavegacionInputDTO inputDTO = new NavegacionInputDTO(
            handle,
            navegacionCurada,
            new HechosFiltrandoInputDTO(
                    new Categoria(categoria),
                    tipoOrigen != null ? ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen.valueOf(tipoOrigen) : null,
                    fechaReporteDesde,
                    fechaReporteHasta,
                    fechaAcontecimientoDesde,
                    fechaAcontecimientoHasta,
                    new Ubicacion(latitud, longitud),
                    radio
            )
    );
    return coleccionServicio.consultarColeccion(inputDTO);
  }

  @PatchMapping("/curar")
  public void curar() {
    this.consensoPlanificador.curarHechos();
  }
}

// TODO: Ver si añadimos endpoints para agregar o quitar fuentes de coleccion

