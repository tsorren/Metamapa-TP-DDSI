package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.ConsensoPlanificador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ColeccionRepositorio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.ColeccionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ConsensoPlanificadorTest {

  private ColeccionRepositorio coleccionRepositorio;
  private ColeccionServicio coleccionServicio;
  private FuenteServicio fuenteServicio;
  private ConsensoPlanificador planificador;

  @BeforeEach
  void setUp() {
    coleccionRepositorio = mock(ColeccionRepositorio.class);
    coleccionServicio = mock(ColeccionServicio.class);
    fuenteServicio = mock(FuenteServicio.class);

    planificador = new ConsensoPlanificador(fuenteServicio, coleccionRepositorio, coleccionServicio);
  }

  @Test
  void curarHechos_EjecutaCuracionSegunTipoConsenso() {
    // Arrange
    Hecho hecho1 = mock(Hecho.class);
    Hecho hecho2 = mock(Hecho.class);
    Fuente fuente = mock(Fuente.class);

    when(hecho1.cumple(TipoConsenso.ABSOLUTO)).thenReturn(false);
    when(hecho2.cumple(TipoConsenso.ABSOLUTO)).thenReturn(true); // este no se cura

    Coleccion coleccion = mock(Coleccion.class);
    when(coleccion.getAlgoritmoConsenso()).thenReturn(TipoConsenso.ABSOLUTO);
    when(coleccion.getIdentificador()).thenReturn(1L);

    when(coleccionRepositorio.encontrarTodas()).thenReturn(List.of(coleccion));
    when(coleccionServicio.hechosDeColeccion(1L)).thenReturn(List.of(hecho1, hecho2));
    when(fuenteServicio.encontrarTodas()).thenReturn(List.of(fuente));

    // Act
    planificador.curarHechos();

    // Assert
    verify(hecho1, atLeastOnce()).quitarCurado(); // todos los hechos se descuran al principio
    verify(hecho2, atLeastOnce()).quitarCurado();
    verify(hecho1, atLeastOnce()).cumple(TipoConsenso.ABSOLUTO);
    verify(hecho2, atLeastOnce()).cumple(TipoConsenso.ABSOLUTO);
    verify(hecho1, atLeastOnce()).curar(any()); // solo se cura el que no cumple
    verify(hecho2, never()).curar(any());
  }
}
