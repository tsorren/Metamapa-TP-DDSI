package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.SolicitudProcesamientoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeModificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeModificacionServicio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes-modificacion")
public class SolicitudDeModificacionControlador {
  private final ISolicitudDeModificacionServicio solicitudDeModificacionServicio;

  public SolicitudDeModificacionControlador(ISolicitudDeModificacionServicio solicitudDeModificacionServicio) {
    this.solicitudDeModificacionServicio = solicitudDeModificacionServicio;
  }

  @GetMapping
  public List<SolicitudDeModificacionOutputDTO> encontrarTodas(
          @RequestParam(value = "estado", required = false) TipoEstadoRevision estado
  ) {
    return this.solicitudDeModificacionServicio.encontrarTodas(estado);
  }

  @PostMapping()
  public SolicitudDeModificacionOutputDTO crear(@RequestBody HechoModificacionDTO input) {
    return this.solicitudDeModificacionServicio.crear(input);
  }

  @PatchMapping("/{idSolicitud}")
  public SolicitudDeModificacionOutputDTO procesar(@PathVariable Long idSolicitud, @RequestBody SolicitudProcesamientoInputDTO input) {
    return this.solicitudDeModificacionServicio.procesar(
            idSolicitud,
            input.getIdResponsable(),
            input.getNuevoEstado(),
            input.getFeedback()
    );
  }
}
