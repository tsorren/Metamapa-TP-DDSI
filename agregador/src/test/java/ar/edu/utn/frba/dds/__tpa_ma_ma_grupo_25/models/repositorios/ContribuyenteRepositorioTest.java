package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ContribuyenteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContribuyenteRepositorioTest {

  private ContribuyenteRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new ContribuyenteRepositorio();
  }

  @Test
  void guardar_contribuyenteSinId_leAsignaIdentificador() {
    Contribuyente contribuyente = Contribuyente.crear("Juan", "Pérez", 30);

    assertNull(contribuyente.getIdentificador());

    repositorio.guardar(contribuyente);

    assertNotNull(contribuyente.getIdentificador());
    assertEquals(1L, contribuyente.getIdentificador());
  }

  @Test
  void guardar_contribuyenteConId_conservaElId() {
    Contribuyente contribuyente = Contribuyente.crear("Ana", "López", 25);
    contribuyente.setIdentificador(88L);

    repositorio.guardar(contribuyente);

    assertEquals(88L, contribuyente.getIdentificador());
    assertEquals(contribuyente, repositorio.encontrarPorId(88L));
  }

  @Test
  void encontrarPorId_existente_devuelveCorrecto() {
    Contribuyente contribuyente = Contribuyente.crear("Carlos", "Gómez", 40);
    repositorio.guardar(contribuyente);

    Contribuyente encontrado = repositorio.encontrarPorId(contribuyente.getIdentificador());

    assertNotNull(encontrado);
    assertEquals(contribuyente, encontrado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    assertNull(repositorio.encontrarPorId(999L));
  }

  @Test
  void encontrarTodas_retornaTodosLosContribuyentes() {
    Contribuyente c1 = Contribuyente.crear("A");
    Contribuyente c2 = Contribuyente.crear("B", "ApellidoB");
    Contribuyente c3 = Contribuyente.crear("C", "ApellidoC", 22);

    repositorio.guardar(c1);
    repositorio.guardar(c2);
    repositorio.guardar(c3);

    List<Contribuyente> todos = repositorio.encontrarTodas();

    assertEquals(3, todos.size());
    assertTrue(todos.contains(c1));
    assertTrue(todos.contains(c2));
    assertTrue(todos.contains(c3));
  }

  @Test
  void idsAsignadosIncrementalmente() {
    Contribuyente c1 = Contribuyente.crear("X");
    Contribuyente c2 = Contribuyente.crear("Y");

    repositorio.guardar(c1);
    repositorio.guardar(c2);

    assertEquals(1L, c1.getIdentificador());
    assertEquals(2L, c2.getIdentificador());
  }
}

