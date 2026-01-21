package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.Notificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface INotificacionRepositorioBD extends JpaRepository<Notificacion, Long> {
  List<Notificacion> findByAdministrador(Administrador administrador);
  List<Notificacion> findByContribuyente(Contribuyente contribuyente);
  List<Notificacion> findByAdministradorAndLeida(Administrador administrador, Boolean leida);
  List<Notificacion> findByContribuyenteAndLeida(Contribuyente contribuyente, Boolean leida);
  List<Notificacion> findByAdministradorOrderByFechaCreacionDesc(Administrador administrador);
  List<Notificacion> findByContribuyenteOrderByFechaCreacionDesc(Contribuyente contribuyente);
}

