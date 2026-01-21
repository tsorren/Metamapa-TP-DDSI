package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.SolicitudEliminacionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SolicitudEliminacionRepositorioTest {

  private SolicitudEliminacionRepositorio repositorio;

  private Hecho hechoMock = Hecho.builder().build();
  private Contribuyente contribuyenteMock = Contribuyente.crear("Juan", "Perez");

  @BeforeEach
  void setUp() {
    repositorio = new SolicitudEliminacionRepositorio();
  }

  private SolicitudDeEliminacion crearSolicitud() {
    return new SolicitudDeEliminacion(hechoMock, "Justificación válida", contribuyenteMock);
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    SolicitudDeEliminacion solicitud = crearSolicitud();
    solicitud.setIdentificador(null);

    repositorio.guardar(solicitud);

    assertNotNull(solicitud.getIdentificador());
    assertEquals(1L, solicitud.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    SolicitudDeEliminacion solicitud = crearSolicitud();
    solicitud.setIdentificador(42L);

    repositorio.guardar(solicitud);

    assertEquals(42L, solicitud.getIdentificador());
  }

  @Test
  void encontrarPorId_devuelveSolicitudCorrecta() {
    SolicitudDeEliminacion solicitud = crearSolicitud();
    repositorio.guardar(solicitud);
    Long id = solicitud.getIdentificador();

    SolicitudDeEliminacion encontrada = repositorio.encontrarPorId(id);

    assertNotNull(encontrada);
    assertEquals(solicitud, encontrada);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    SolicitudDeEliminacion encontrada = repositorio.encontrarPorId(999L);
    assertNull(encontrada);
  }

  @Test
  void eliminar_quitaSolicitudDelRepositorio() {
    SolicitudDeEliminacion solicitud = crearSolicitud();
    repositorio.guardar(solicitud);

    repositorio.eliminar(solicitud);

    assertNull(repositorio.encontrarPorId(solicitud.getIdentificador()));
  }

  @Test
  void encontrarTodas_sinEstado_devuelveTodasLasSolicitudes() {
    SolicitudDeEliminacion s1 = crearSolicitud();
    SolicitudDeEliminacion s2 = crearSolicitud();
    repositorio.guardar(s1);
    repositorio.guardar(s2);

    List<SolicitudDeEliminacion> todas = repositorio.encontrarTodas(null);

    assertEquals(2, todas.size());
    assertTrue(todas.contains(s1));
    assertTrue(todas.contains(s2));
  }

  @Test
  void encontrarTodas_conEstado_filtraPorEstado() {
    SolicitudDeEliminacion s1 = crearSolicitud();
    SolicitudDeEliminacion s2 = crearSolicitud();

    s1.setEstadoAtencionSolicitud(EstadoAtencionSolicitud.PENDIENTE);
    s2.setEstadoAtencionSolicitud(EstadoAtencionSolicitud.ACEPTADA);

    repositorio.guardar(s1);
    repositorio.guardar(s2);

    List<SolicitudDeEliminacion> pendientes = repositorio.encontrarTodas(EstadoAtencionSolicitud.PENDIENTE);
    List<SolicitudDeEliminacion> aceptadas = repositorio.encontrarTodas(EstadoAtencionSolicitud.ACEPTADA);

    assertEquals(1, pendientes.size());
    assertTrue(pendientes.contains(s1));

    assertEquals(1, aceptadas.size());
    assertTrue(aceptadas.contains(s2));
  }
}
