package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.SolicitudProcesamientoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes-creacion")
public class SolicitudDeCreacionControlador {
  private final ISolicitudDeCreacionServicio solicitudDeCreacionServicio;

  public SolicitudDeCreacionControlador(ISolicitudDeCreacionServicio solicitudDeCreacionServicio) {
    this.solicitudDeCreacionServicio = solicitudDeCreacionServicio;
  }

  @GetMapping
  public List<SolicitudDeCreacionOutputDTO> encontrarTodas(
          @RequestParam(value = "estado", required = false) TipoEstadoRevision estado
  ) {
    return this.solicitudDeCreacionServicio.encontrarTodas(estado);
  }

  @PatchMapping("/{idSolicitud}")
  public SolicitudDeCreacionOutputDTO procesar(@PathVariable Long idSolicitud, @RequestBody SolicitudProcesamientoInputDTO input) {
    return this.solicitudDeCreacionServicio.procesar(
            idSolicitud,
            input.getIdResponsable(),
            input.getNuevoEstado(),
            input.getFeedback()
    );
  }
}
