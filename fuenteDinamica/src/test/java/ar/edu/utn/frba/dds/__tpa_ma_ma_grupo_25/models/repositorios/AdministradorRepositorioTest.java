package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.AdministradorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("Se guarda un administrador con ID autogenerado")
  void guardarAdministradorGeneraId() {
    Administrador admin = new Administrador();
    assertNull(admin.getIdentificador());

    repositorio.guardar(admin);

    assertNotNull(admin.getIdentificador());
    assertEquals(admin, repositorio.encontrarPorId(admin.getIdentificador()));
  }

  @Test
  @DisplayName("Se pueden recuperar todos los administradores")
  void recuperarTodosLosAdministradores() {
    repositorio.guardar(new Administrador());
    repositorio.guardar(new Administrador());

    List<Administrador> todos = repositorio.encontrarTodas();
    assertEquals(2, todos.size());
  }

  @Test
  @DisplayName("Devuelve null si no existe el administrador")
  void noEncuentraAdministradorInexistente() {
    assertNull(repositorio.encontrarPorId(999L));
  }
}
