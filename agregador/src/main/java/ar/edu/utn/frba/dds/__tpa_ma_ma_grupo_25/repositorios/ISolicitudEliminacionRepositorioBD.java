package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISolicitudEliminacionRepositorioBD extends JpaRepository<SolicitudDeEliminacion, Long> {
  List<SolicitudDeEliminacion> findByEstadoAtencionSolicitud(EstadoAtencionSolicitud esatdo);
}
