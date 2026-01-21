package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeModificacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeModificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;

import java.util.List;

public interface ISolicitudDeModificacionServicio {
  SolicitudDeModificacionOutputDTO crear(HechoModificacionDTO input);

  List<SolicitudDeModificacionOutputDTO> encontrarTodas(TipoEstadoRevision estado);

  SolicitudDeModificacionOutputDTO encontrarPorId(Long identificador);

  SolicitudDeModificacionOutputDTO procesar(Long idSolicitud, Long idResponsable, TipoEstadoRevision nuevoEstado, String Feedback);

  void procesarPendientesDeModificacion();

  SolicitudDeModificacion encontrarPorIdHecho(Long idHecho);

  Boolean estaVencida(SolicitudDeModificacion s);

  Boolean fueCreadaPor(SolicitudDeModificacion s, Long idContribuyente);
}
