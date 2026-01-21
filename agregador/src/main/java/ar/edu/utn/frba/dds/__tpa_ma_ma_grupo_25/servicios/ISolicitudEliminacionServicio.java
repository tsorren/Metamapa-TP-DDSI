package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeEliminacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;

import java.time.LocalDateTime;
import java.util.List;

public interface ISolicitudEliminacionServicio {
  List<SolicitudDeEliminacionOutputDTO> encontrarTodas(EstadoAtencionSolicitud estado);

  SolicitudDeEliminacionOutputDTO procesarSolicitud(Long idSolicitud, Long idResponsable, EstadoAtencionSolicitud estado, String justificacion);

  SolicitudDeEliminacionOutputDTO solicitarEliminacion(Long idHecho, String justficacion, Long idContribuyente);

  void procesarSpam(LocalDateTime ultimoProcesamiento);
}
