package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISolicitudDeCreacionRepositorioBD extends JpaRepository<SolicitudDeCreacion, Long> {
  List<SolicitudDeCreacion> findByEstados_TipoEstado(TipoEstadoRevision estado);

  SolicitudDeCreacion findByHecho_Id(Long idHecho);
}
