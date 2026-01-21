package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.Filtro;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.FiltroCargaEntre;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.CriteriosPertenenciaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CriteriosPertenenciaRepositorioTest {

  private CriteriosPertenenciaRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new CriteriosPertenenciaRepositorio();
  }

  @Test
  void guardar_criterioSinId_asignaIdentificador() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    assertNull(criterio.getIdentificador());

    repositorio.guardar(criterio);

    assertNotNull(criterio.getIdentificador());
    assertEquals(1L, criterio.getIdentificador());
  }

  @Test
  void guardar_criterioConId_conservaIdentificador() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.setIdentificador(99L);

    repositorio.guardar(criterio);

    assertEquals(99L, criterio.getIdentificador());
    assertEquals(criterio, repositorio.encontrarPorId(99L));
  }

  @Test
  void encontrarPorId_existente_devuelveCorrecto() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    repositorio.guardar(criterio);

    CriterioDePertenencia recuperado = repositorio.encontrarPorId(criterio.getIdentificador());

    assertNotNull(recuperado);
    assertEquals(criterio, recuperado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    assertNull(repositorio.encontrarPorId(1000L));
  }

  @Test
  void encontrarTodos_devuelveTodosLosCriteriosGuardados() {
    CriterioDePertenencia c1 = new CriterioDePertenencia();
    CriterioDePertenencia c2 = new CriterioDePertenencia();

    repositorio.guardar(c1);
    repositorio.guardar(c2);

    List<CriterioDePertenencia> todos = repositorio.encontrarTodos();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(c1));
    assertTrue(todos.contains(c2));
  }

  @Test
  void idsAsignadosIncrementalmente() {
    CriterioDePertenencia c1 = new CriterioDePertenencia();
    CriterioDePertenencia c2 = new CriterioDePertenencia();

    repositorio.guardar(c1);
    repositorio.guardar(c2);

    assertEquals(1L, c1.getIdentificador());
    assertEquals(2L, c2.getIdentificador());
  }

  @Test
  void agregarYQuitarFiltros_funcionaCorrectamente() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();

    Filtro filtroMock = mock(Filtro.class);
    criterio.agregarFiltro(filtroMock);

    assertEquals(1, criterio.getFiltros().size());
    assertTrue(criterio.getFiltros().contains(filtroMock));

    criterio.quitarFiltro(filtroMock);

    assertTrue(criterio.getFiltros().isEmpty());
  }

  @Test
  void cumpleConHecho_devuelveTrue_siTodosLosFiltrosLoCumplen_conFiltroCargaEntre() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();

    LocalDateTime inicio = LocalDateTime.now().minusDays(1);
    LocalDateTime fin = LocalDateTime.now().plusDays(1);

    FiltroCargaEntre filtro = new FiltroCargaEntre(inicio, fin);

    Hecho hecho = mock(Hecho.class);
    when(hecho.getFechaDeCarga()).thenReturn(LocalDateTime.now());

    criterio.agregarFiltro(filtro);

    assertTrue(criterio.cumpleCon(hecho));
  }
}
