package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.AdministradorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdministradorRepositorioTest {

  private AdministradorRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new AdministradorRepositorio();
  }

  @Test
  void guardar_administradorSinId_leAsignaIdentificador() {
    Administrador admin = new Administrador();

    repositorio.guardar(admin);

    assertNotNull(admin.getIdentificador());
    assertEquals(1L, admin.getIdentificador());
  }

  @Test
  void guardar_administradorConId_conservaElId() {
    Administrador admin = new Administrador();
    admin.setIdentificador(100L);

    repositorio.guardar(admin);

    assertEquals(100L, admin.getIdentificador());
    assertEquals(admin, repositorio.encontrarPorId(100L));
  }

  @Test
  void encontrarPorId_existente_retornaCorrectamente() {
    Administrador admin = new Administrador();
    repositorio.guardar(admin);

    Administrador encontrado = repositorio.encontrarPorId(admin.getIdentificador());

    assertNotNull(encontrado);
    assertEquals(admin, encontrado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    assertNull(repositorio.encontrarPorId(999L));
  }

  @Test
  void encontrarTodas_retornaTodosLosAdministradores() {
    Administrador admin1 = new Administrador();
    Administrador admin2 = new Administrador();

    repositorio.guardar(admin1);
    repositorio.guardar(admin2);

    List<Administrador> todos = repositorio.encontrarTodas();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(admin1));
    assertTrue(todos.contains(admin2));
  }

  @Test
  void guardar_multiplesAdministradores_idsIncrementan() {
    Administrador admin1 = new Administrador();
    Administrador admin2 = new Administrador();
    Administrador admin3 = new Administrador();

    repositorio.guardar(admin1);
    repositorio.guardar(admin2);
    repositorio.guardar(admin3);

    assertEquals(1L, admin1.getIdentificador());
    assertEquals(2L, admin2.getIdentificador());
    assertEquals(3L, admin3.getIdentificador());
  }
}
