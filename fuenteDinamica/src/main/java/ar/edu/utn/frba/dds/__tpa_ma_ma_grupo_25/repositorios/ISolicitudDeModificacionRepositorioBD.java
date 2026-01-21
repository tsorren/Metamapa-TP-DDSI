package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeModificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISolicitudDeModificacionRepositorioBD extends JpaRepository<SolicitudDeModificacion, Long> {
  List<SolicitudDeModificacion> findByEstados_TipoEstado(TipoEstadoRevision estado);

  SolicitudDeModificacion findByHecho_Id(Long idHecho);
}
