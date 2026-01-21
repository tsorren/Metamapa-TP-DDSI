package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitudes;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SolicitudDeEliminacionTest {

  private Hecho hecho;
  private Contribuyente contribuyente;
  private Administrador administrador;
  private SolicitudDeEliminacion solicitud;

  @BeforeEach
  public void setUp() {
    hecho = Hecho.crear(
            1L,
            "Título",
            "Descripción",
            new Categoria("Transporte"),
            new Ubicacion(-34.61, -58.38),
            LocalDate.of(2024, 1, 1),
            LocalDateTime.of(2024, 1, 1, 10, 0),
            LocalDateTime.of(2024, 1, 1, 10, 0),
            null,
            TipoOrigen.DATASET,
            Collections.emptyList(),
            true
    );

    contribuyente = Contribuyente.crear("pep", "guardiola", 34);
    administrador = new Administrador();

    solicitud = new SolicitudDeEliminacion(hecho, "Justificación válida", contribuyente);
  }

  @Test
  public void seCreaConEstadoPendienteYFechaDeCreacion() {
    assertEquals(EstadoAtencionSolicitud.PENDIENTE, solicitud.getEstadoAtencionSolicitud());
    assertNotNull(solicitud.getFechaCreacion());
    assertEquals("Justificación válida", solicitud.getJustificacion());
    assertEquals(hecho, solicitud.getHecho());
    assertEquals(contribuyente, solicitud.getContribuyente());
  }

  @Test
  public void marcarAceptadaCambiaEstadoYHaceInvisibleElHecho() {
    solicitud.marcarAceptada(administrador);

    assertEquals(EstadoAtencionSolicitud.ACEPTADA, solicitud.getEstadoAtencionSolicitud());
    assertEquals(administrador, solicitud.getAdministradorResponsable());
    assertNotNull(solicitud.getFechaResolucion());
    assertFalse(hecho.getVisible());
  }

  @Test
  public void marcarRechazadaCambiaEstadoYAgregaJustificacion() {
    solicitud.marcarRechazada(administrador, "No es válida");

    assertEquals(EstadoAtencionSolicitud.RECHAZADA, solicitud.getEstadoAtencionSolicitud());
    assertEquals(administrador, solicitud.getAdministradorResponsable());
    assertEquals("No es válida", solicitud.getJustificacionRechazo());
    assertNotNull(solicitud.getFechaResolucion());
    assertTrue(hecho.getVisible()); // El hecho no debe ocultarse
  }

  @Test
  public void noSePuedeAceptarSiYaFueAceptada() {
    solicitud.marcarAceptada(administrador);
    solicitud.marcarAceptada(new Administrador());

    assertEquals(EstadoAtencionSolicitud.ACEPTADA, solicitud.getEstadoAtencionSolicitud());
    assertEquals(administrador, solicitud.getAdministradorResponsable());
  }

  @Test
  public void noSePuedeRechazarSiYaFueAceptada() {
    solicitud.marcarAceptada(administrador);
    solicitud.marcarRechazada(new Administrador(), "Motivo inválido");

    assertEquals(EstadoAtencionSolicitud.ACEPTADA, solicitud.getEstadoAtencionSolicitud());
    assertNull(solicitud.getJustificacionRechazo());
  }

  @Test
  public void noSePuedeAceptarSiYaFueRechazada() {
    solicitud.marcarRechazada(administrador, "Motivo inválido");
    solicitud.marcarAceptada(new Administrador());

    assertEquals(EstadoAtencionSolicitud.RECHAZADA, solicitud.getEstadoAtencionSolicitud());
    assertEquals("Motivo inválido", solicitud.getJustificacionRechazo());
  }
}
