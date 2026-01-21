package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.FiltroRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FiltroRepositorioTest {

  private FiltroRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new FiltroRepositorio();
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    Filtro filtro = new FiltroTitulo("titulo");
    filtro.setIdentificador(null);

    repositorio.guardar(filtro);

    assertNotNull(filtro.getIdentificador());
    assertEquals(1L, filtro.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    Filtro filtro = new FiltroTitulo("titulo");
    filtro.setIdentificador(100L);

    repositorio.guardar(filtro);

    assertEquals(100L, filtro.getIdentificador());
  }

  @Test
  void encontrarPorId_existente_devuelveFiltro() {
    Filtro filtro = new FiltroCategoria(new Categoria("Salud"));
    repositorio.guardar(filtro);
    Long id = filtro.getIdentificador();

    Filtro resultado = repositorio.encontrarPorId(id);

    assertNotNull(resultado);
    assertEquals(filtro, resultado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    Filtro resultado = repositorio.encontrarPorId(999L);
    assertNull(resultado);
  }

  @Test
  void encontrarTodos_devuelveTodosLosFiltrosGuardados() {
    Filtro f1 = new FiltroTitulo("titulo1");
    Filtro f2 = new FiltroDescripcion("desc2");
    repositorio.guardar(f1);
    repositorio.guardar(f2);

    List<Filtro> todos = repositorio.encontrarTodos();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(f1));
    assertTrue(todos.contains(f2));
  }

  @Test
  void idsIncrementales_alGuardarMultiplesFiltros() {
    Filtro f1 = new FiltroTitulo("t1");
    Filtro f2 = new FiltroCategoria(new Categoria("Salud"));

    repositorio.guardar(f1);
    repositorio.guardar(f2);

    assertEquals(1L, f1.getIdentificador());
    assertEquals(2L, f2.getIdentificador());
  }

  // --- Tests básicos de cumplimiento de filtros concretos ---

  @Test
  void filtroTitulo_cumpleCon_funcionaCorrectamente() {
    FiltroTitulo filtro = new FiltroTitulo("MiTitulo");

    Hecho hecho = new Hecho();
    hecho.setTitulo("MiTitulo");

    assertTrue(filtro.cumpleCon(hecho));

    hecho.setTitulo("OtroTitulo");
    assertFalse(filtro.cumpleCon(hecho));
  }

  @Test
  void filtroCategoria_cumpleCon_funcionaCorrectamente() {
    Categoria categoria = new Categoria("Salud");
    FiltroCategoria filtro = new FiltroCategoria(categoria);

    Hecho hecho = new Hecho();
    hecho.setCategoria(new Categoria("Salud"));

    assertTrue(filtro.cumpleCon(hecho));

    hecho.setCategoria(new Categoria("Educacion"));
    assertFalse(filtro.cumpleCon(hecho));
  }

  @Test
  void filtroCargaEntre_cumpleCon_funcionaCorrectamente() {
    LocalDateTime inicio = LocalDateTime.now().minusDays(1);
    LocalDateTime fin = LocalDateTime.now().plusDays(1);
    FiltroCargaEntre filtro = new FiltroCargaEntre(inicio, fin);

    Hecho hecho = new Hecho();
    hecho.setFechaDeCarga(LocalDateTime.now());

    assertTrue(filtro.cumpleCon(hecho));

    hecho.setFechaDeCarga(inicio.minusSeconds(1));
    assertFalse(filtro.cumpleCon(hecho));

    hecho.setFechaDeCarga(fin.plusSeconds(1));
    assertFalse(filtro.cumpleCon(hecho));
  }

  @Test
  void filtroUbicacion_cumpleCon_funcionaCorrectamente() {
    Ubicacion centro = new Ubicacion(-34.6, -58.4);
    FiltroUbicacion filtro = new FiltroUbicacion(centro, 10.0);

    Hecho hecho = new Hecho();
    hecho.setUbicacion(new Ubicacion(-34.6, -58.4));
    assertTrue(filtro.cumpleCon(hecho));

    hecho.setUbicacion(new Ubicacion(-34.7, -58.5));
    assertTrue(filtro.cumpleCon(hecho));

    hecho.setUbicacion(new Ubicacion(-35.6, -58.5)); // Muy lejos
    assertFalse(filtro.cumpleCon(hecho));
  }
}
