package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ModuloFuenteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ModuloFuenteRepositorioTest {

  private ModuloFuenteRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new ModuloFuenteRepositorio();
  }

  private ModuloFuente crearModuloFuenteEjemplo() {
    WebClient webClientMock = mock(WebClient.class);
    return new ModuloFuente(TipoFuente.ESTATICA, "http://localhost/api", webClientMock);
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    ModuloFuente modulo = crearModuloFuenteEjemplo();
    modulo.setIdentificador(null);

    repositorio.guardar(modulo);

    assertNotNull(modulo.getIdentificador());
    assertEquals(1L, modulo.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    ModuloFuente modulo = crearModuloFuenteEjemplo();
    modulo.setIdentificador(42L);

    repositorio.guardar(modulo);

    assertEquals(42L, modulo.getIdentificador());
  }

  @Test
  void encontrarPorId_devuelveModuloCorrecto() {
    ModuloFuente modulo = crearModuloFuenteEjemplo();
    repositorio.guardar(modulo);
    Long id = modulo.getIdentificador();

    ModuloFuente encontrado = repositorio.encontrarPorId(id);

    assertNotNull(encontrado);
    assertEquals(modulo, encontrado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    ModuloFuente encontrado = repositorio.encontrarPorId(999L);
    assertNull(encontrado);
  }

  @Test
  void encontrarTodas_devuelveTodosLosModulos() {
    ModuloFuente modulo1 = crearModuloFuenteEjemplo();
    ModuloFuente modulo2 = crearModuloFuenteEjemplo();

    repositorio.guardar(modulo1);
    repositorio.guardar(modulo2);

    List<ModuloFuente> todos = repositorio.encontrarTodas();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(modulo1));
    assertTrue(todos.contains(modulo2));
  }

  @Test
  void idsIncrementales_alGuardarMultiplesModulos() {
    ModuloFuente modulo1 = crearModuloFuenteEjemplo();
    ModuloFuente modulo2 = crearModuloFuenteEjemplo();

    repositorio.guardar(modulo1);
    repositorio.guardar(modulo2);

    assertEquals(1L, modulo1.getIdentificador());
    assertEquals(2L, modulo2.getIdentificador());
  }
}

