package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class SolicitudesPlanificadorTest {

  private ISolicitudDeCreacionServicio solicitudDeCreacionServicioMock;
  private solicitudesPlanificador planificador;

  @BeforeEach
  public void setup() {
    solicitudDeCreacionServicioMock = mock(ISolicitudDeCreacionServicio.class);
    planificador = new solicitudesPlanificador(solicitudDeCreacionServicioMock);
  }

  @Test
  public void testProcesarSolicitudesPendientes() {
    // Ejecutamos el método planificado directamente
    planificador.procesarSolicitudesPendientes();

    // Verificamos que se llamó al método del servicio
    verify(solicitudDeCreacionServicioMock, times(1)).procesarPendientesDeModificacion();
  }
}

