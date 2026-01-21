package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.FuenteEstaticaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuenteEstaticaRepositorioTest {

  private FuenteEstaticaRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new FuenteEstaticaRepositorio();
  }

  @Test
  void guardaUnaFuenteYLeAsignaUnId() {
    FuenteEstatica fuente = new FuenteEstatica(Paths.get("datos/Escenario_2.csv"));
    assertNull(fuente.getIdentificador());

    repositorio.guardar(fuente);

    assertNotNull(fuente.getIdentificador());
    assertEquals(fuente, repositorio.encontrarPorId(fuente.getIdentificador()));
  }

  @Test
  void guardaMultiplesFuentesYLasRecupera() {
    FuenteEstatica f1 = new FuenteEstatica(Paths.get("datos/Escenario_2.csv"));
    FuenteEstatica f2 = new FuenteEstatica(Paths.get("datos/Escenario_2.csv"));

    repositorio.guardar(f1);
    repositorio.guardar(f2);

    List<FuenteEstatica> todas = repositorio.encontrarTodas();

    assertEquals(2, todas.size());
    assertTrue(todas.contains(f1));
    assertTrue(todas.contains(f2));
  }

  @Test
  void cargaHechosYLosAsociaALaFuente() {
    FuenteEstatica fuente = new FuenteEstatica(Paths.get("datos/Escenario_2.csv"));
    repositorio.guardar(fuente);

    Long id = fuente.getIdentificador();
    repositorio.cargarHecho(id, 100L);
    repositorio.cargarHecho(id, 200L);

    List<Long> hechos = repositorio.hechosDeFuente(id);

    assertNotNull(hechos);
    assertEquals(2, hechos.size());
    assertTrue(hechos.contains(100L));
    assertTrue(hechos.contains(200L));
  }

  @Test
  void devuelveNullSiNoExisteLaFuente() {
    assertNull(repositorio.encontrarPorId(999L));
  }

  @Test
  void devuelveNullSiNoTieneHechosAsociados() {
    FuenteEstatica fuente = new FuenteEstatica(Paths.get("datos/Escenario_2.csv"));
    repositorio.guardar(fuente);

    assertNull(repositorio.hechosDeFuente(fuente.getIdentificador()));
  }
}
