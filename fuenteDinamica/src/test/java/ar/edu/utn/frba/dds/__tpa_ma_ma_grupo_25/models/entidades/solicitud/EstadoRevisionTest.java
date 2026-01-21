package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EstadoRevisionTest {

  @Test
  @DisplayName("Un estado recién creado no fue procesado")
  void estadoNuevoNoProcesado() {
    EstadoRevision estado = new EstadoRevision(TipoEstadoRevision.PENDIENTE, "esperando");
    assertFalse(estado.fueProcesado());
    assertNull(estado.getFechaResolucion());
    assertNull(estado.getAdministradorResponsable());
  }

  @Test
  @DisplayName("Al procesar un estado se asigna fecha y administrador")
  void procesarEstadoAsignaAdministradorYFecha() {
    EstadoRevision estado = new EstadoRevision(TipoEstadoRevision.ACEPTADA, "aceptado");
    Administrador admin = mock(Administrador.class);

    estado.procesar(admin);

    assertTrue(estado.fueProcesado());
    assertNotNull(estado.getFechaResolucion());
    assertEquals(admin, estado.getAdministradorResponsable());
  }

  @Test
  @DisplayName("No se vuelve a procesar un estado ya procesado")
  void noReprocesaEstadoYaProcesado() {
    EstadoRevision estado = new EstadoRevision(TipoEstadoRevision.RECHAZADA, "rechazado");
    Administrador admin1 = mock(Administrador.class);
    Administrador admin2 = mock(Administrador.class);

    estado.procesar(admin1);
    var fecha = estado.getFechaResolucion();
    estado.procesar(admin2);

    assertEquals(admin1, estado.getAdministradorResponsable());
    assertEquals(fecha, estado.getFechaResolucion());
  }
}
