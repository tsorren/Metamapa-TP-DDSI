package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;

import java.util.List;

public interface ISolicitudDeCreacionServicio {
  SolicitudDeCreacion crear(Hecho hecho, Long idContribuyente);

  List<SolicitudDeCreacionOutputDTO> encontrarTodas(TipoEstadoRevision estado);

  SolicitudDeCreacionOutputDTO encontrarPorId(Long identificador);

  SolicitudDeCreacionOutputDTO procesar(Long idSolicitud, Long idResponsable, TipoEstadoRevision nuevoEstado, String Feedback);

  void procesarPendientesDeModificacion();

  SolicitudDeCreacion encontrarPorIdHecho(Long idHecho);

  Boolean estaVencida(SolicitudDeCreacion s);

  Boolean fueCreadaPor(SolicitudDeCreacion s, Long idContribuyente);
}
