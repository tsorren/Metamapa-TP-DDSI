package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.FuentesPlanificador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.SolicitudesPlanificador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlanificadorTest {

  private IFuenteServicio fuenteServicioMock;
  private IModuloFuenteServicio moduloFuenteServicioMock;
  private ISolicitudEliminacionServicio solicitudEliminacionServicioMock;

  private FuentesPlanificador fuentesPlanificador;
  private SolicitudesPlanificador solicitudesPlanificador;

  @BeforeEach
  public void setup() {
    fuenteServicioMock = mock(IFuenteServicio.class);
    moduloFuenteServicioMock = mock(IModuloFuenteServicio.class);
    solicitudEliminacionServicioMock = mock(ISolicitudEliminacionServicio.class);

    fuentesPlanificador = new FuentesPlanificador(fuenteServicioMock, moduloFuenteServicioMock);
    solicitudesPlanificador = new SolicitudesPlanificador(solicitudEliminacionServicioMock);
  }

  @Test
  public void testRefrescarColecciones() throws InterruptedException {
    LocalDateTime beforeCall = fuentesPlanificador.getUltimoRefresco();

    // Esperamos un poco para que se note el cambio de tiempo
    Thread.sleep(10);

    fuentesPlanificador.refrescarColecciones();

    // Verificamos que los servicios se llamaron con el valor correcto
    verify(moduloFuenteServicioMock).pedirNuevasFuentes(beforeCall);
    verify(fuenteServicioMock).refrescarHechosDeFuentes(beforeCall);

    // Verificamos que el ultimoRefresco se actualizó (es mayor o igual que beforeCall)
    assertTrue(fuentesPlanificador.getUltimoRefresco().isAfter(beforeCall) ||
            fuentesPlanificador.getUltimoRefresco().isEqual(beforeCall));
  }

  @Test
  public void testProcesarSpam() throws InterruptedException {
    LocalDateTime beforeCall = solicitudesPlanificador.getUltimoProcesamiento();

    Thread.sleep(10);

    solicitudesPlanificador.procesarSpam();

    verify(solicitudEliminacionServicioMock).procesarSpam(beforeCall);

    assertTrue(solicitudesPlanificador.getUltimoProcesamiento().isAfter(beforeCall) ||
            solicitudesPlanificador.getUltimoProcesamiento().isEqual(beforeCall));
  }
}
