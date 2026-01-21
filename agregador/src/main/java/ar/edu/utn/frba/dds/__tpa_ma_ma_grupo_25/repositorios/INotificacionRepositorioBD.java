package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.notificacion.Notificacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface INotificacionRepositorioBD extends JpaRepository<Notificacion, Long> {
  // Consultas básicas (mantener para compatibilidad)
  List<Notificacion> findByAdministrador(Administrador administrador);
  List<Notificacion> findByContribuyente(Contribuyente contribuyente);

  // Consultas con filtro de leída
  List<Notificacion> findByAdministradorAndLeida(Administrador administrador, Boolean leida);
  List<Notificacion> findByContribuyenteAndLeida(Contribuyente contribuyente, Boolean leida);

  // Consultas ordenadas
  List<Notificacion> findByAdministradorOrderByFechaCreacionDesc(Administrador administrador);
  List<Notificacion> findByContribuyenteOrderByFechaCreacionDesc(Contribuyente contribuyente);

  // Consultas optimizadas con FETCH JOIN para evitar lazy loading issues
  @Query("SELECT n FROM Notificacion n WHERE n.administrador = :admin AND n.leida = :leida ORDER BY n.fechaCreacion DESC")
  List<Notificacion> findByAdministradorAndLeidaOptimized(@Param("admin") Administrador administrador, @Param("leida") Boolean leida);

  @Query("SELECT n FROM Notificacion n WHERE n.contribuyente = :contribuyente AND n.leida = :leida ORDER BY n.fechaCreacion DESC")
  List<Notificacion> findByContribuyenteAndLeidaOptimized(@Param("contribuyente") Contribuyente contribuyente, @Param("leida") Boolean leida);

  // Consultas con límite (para paginación ligera)
  @Query("SELECT n FROM Notificacion n WHERE n.administrador = :admin ORDER BY n.fechaCreacion DESC")
  List<Notificacion> findTopByAdministrador(@Param("admin") Administrador administrador, Pageable pageable);

  @Query("SELECT n FROM Notificacion n WHERE n.contribuyente = :contribuyente ORDER BY n.fechaCreacion DESC")
  List<Notificacion> findTopByContribuyente(@Param("contribuyente") Contribuyente contribuyente, Pageable pageable);

  // Conteo rápido sin cargar entidades
  @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.administrador = :admin AND n.leida = false")
  long countNoLeidasByAdministrador(@Param("admin") Administrador administrador);

  @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.contribuyente = :contribuyente AND n.leida = false")
  long countNoLeidasByContribuyente(@Param("contribuyente") Contribuyente contribuyente);
}

