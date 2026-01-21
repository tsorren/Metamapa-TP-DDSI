package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ProcesamientoSolicitudInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.SolicitudInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeEliminacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/solicitudes-eliminacion")
public class AdminSolicitudDeEliminacionControlador {
  private final ISolicitudEliminacionServicio solicitudEliminacionServicio;

  public AdminSolicitudDeEliminacionControlador(ISolicitudEliminacionServicio solicitudEliminacionServicio) {
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

  @PatchMapping("/{idSolicitud}")
  public SolicitudDeEliminacionOutputDTO procesarSolicitud(
          @PathVariable("idSolicitud") Long idSolicitud,
          @RequestBody ProcesamientoSolicitudInputDTO input) {
    return this.solicitudEliminacionServicio.procesarSolicitud(
            idSolicitud, input.getIdResponsable(), input.getEstado(), input.getJustificacion());
  }
}
