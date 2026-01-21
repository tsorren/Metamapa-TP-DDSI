package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SolicitudDeCreacionTest {

  private Hecho hecho;
  private Contribuyente contribuyente;
  private Administrador admin;

  @BeforeEach
  void setUp() {
    hecho = mock(Hecho.class);
    contribuyente = mock(Contribuyente.class);
    admin = mock(Administrador.class);
  }

  @Test
  @DisplayName("Solicitud inicia con estado pendiente")
  void estadoInicialEsPendiente() {
    SolicitudDeCreacion solicitud = new SolicitudDeCreacion(hecho, contribuyente);
    EstadoRevision estadoInicial = solicitud.estadoActual();
    assertEquals(TipoEstadoRevision.PENDIENTE, estadoInicial.getTipoEstado());
  }

  @Test
  @DisplayName("Una solicitud se cierra al aceptar o rechazar")
  void solicitudSeCierraConEstadoFinal() {
    SolicitudDeCreacion solicitud = new SolicitudDeCreacion(hecho, contribuyente);

    solicitud.procesar(admin, TipoEstadoRevision.ACEPTADA, "Aprobado");
    assertTrue(solicitud.cerrada());

    SolicitudDeCreacion otra = new SolicitudDeCreacion(hecho, contribuyente);
    otra.procesar(admin, TipoEstadoRevision.RECHAZADA, "Rechazado");
    assertTrue(otra.cerrada());
  }

  @Test
  @DisplayName("No se cambia estado si ya está cerrada")
  void noProcesaSiYaCerrada() {
    SolicitudDeCreacion solicitud = new SolicitudDeCreacion(hecho, contribuyente);
    solicitud.procesar(admin, TipoEstadoRevision.ACEPTADA, "Aprobado");
    int estadosAntes = solicitud.getEstados().size();

    solicitud.procesar(admin, TipoEstadoRevision.RECHAZADA, "Ignorado");

    assertEquals(estadosAntes, solicitud.getEstados().size());
    assertEquals(TipoEstadoRevision.ACEPTADA, solicitud.estadoActual().getTipoEstado());
  }

  @Test
  @DisplayName("Devuelve null si no hay estado anterior")
  void estadoAnteriorEsNullSiEsPrimero() {
    SolicitudDeCreacion solicitud = new SolicitudDeCreacion(hecho, contribuyente);
    assertNull(solicitud.estadoAnterior());
    assertNull(solicitud.ultimaFechaProcesamiento());
  }

  @Test
  @DisplayName("Devuelve la fecha de resolución del estado anterior si existe")
  void ultimaFechaProcesamientoValida() {
    SolicitudDeCreacion solicitud = new SolicitudDeCreacion(hecho, contribuyente);
    solicitud.procesar(admin, TipoEstadoRevision.PENDIENTE_DE_MODIFICACION, "Falta info");

    var fechaAnterior = solicitud.estadoAnterior().getFechaResolucion();
    assertEquals(fechaAnterior, solicitud.ultimaFechaProcesamiento());
  }
}
