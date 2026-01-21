package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.SolicitudInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeEliminacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes-eliminacion")
public class PublicSolicitudDeEliminacionControlador {
  private final ISolicitudEliminacionServicio solicitudEliminacionServicio;

  public PublicSolicitudDeEliminacionControlador(ISolicitudEliminacionServicio solicitudEliminacionServicio) {
    this.solicitudEliminacionServicio = solicitudEliminacionServicio;
  }

  @PostMapping
  public SolicitudDeEliminacionOutputDTO crear(@RequestBody SolicitudInputDTO input) {
    return this.solicitudEliminacionServicio.solicitarEliminacion(
            input.getIdHecho(), input.getJustificacion(), input.getIdContribuyente());
  }

  @GetMapping
  public List<SolicitudDeEliminacionOutputDTO> encontrarTodas(
          @RequestParam(value = "estado", required = false) EstadoAtencionSolicitud estado) {
    return this.solicitudEliminacionServicio.encontrarTodas(estado);
  }
}
