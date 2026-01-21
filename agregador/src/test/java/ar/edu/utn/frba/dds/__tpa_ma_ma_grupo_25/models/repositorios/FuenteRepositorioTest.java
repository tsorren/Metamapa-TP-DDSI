package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.FuenteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuenteRepositorioTest {

  private FuenteRepositorio repositorio;
  private ModuloFuente moduloMock;

  @BeforeEach
  void setUp() {
    repositorio = new FuenteRepositorio();
    // Como ModuloFuente tiene WebClient, creamos un mock o instancia simple para probar
    moduloMock = new ModuloFuente(TipoFuente.ESTATICA, "http://localhost", WebClient.create());
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    Fuente fuente = new Fuente(moduloMock, 123L, LocalDateTime.now());
    fuente.setIdentificador(null);

    repositorio.guardar(fuente);

    assertNotNull(fuente.getIdentificador());
    assertEquals(1L, fuente.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    Fuente fuente = new Fuente(moduloMock, 123L, LocalDateTime.now());
    fuente.setIdentificador(99L);

    repositorio.guardar(fuente);

    assertEquals(99L, fuente.getIdentificador());
  }

  @Test
  void encontrarPorId_existente_devuelveFuente() {
    Fuente fuente = new Fuente(moduloMock, 123L, LocalDateTime.now());
    repositorio.guardar(fuente);
    Long id = fuente.getIdentificador();

    Fuente resultado = repositorio.encontrarPorId(id);

    assertNotNull(resultado);
    assertEquals(fuente, resultado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    Fuente resultado = repositorio.encontrarPorId(999L);
    assertNull(resultado);
  }

  @Test
  void encontrarTodas_devuelveTodasLasFuentesGuardadas() {
    Fuente f1 = new Fuente(moduloMock, 1L, LocalDateTime.now());
    Fuente f2 = new Fuente(moduloMock, 2L, LocalDateTime.now());
    repositorio.guardar(f1);
    repositorio.guardar(f2);

    List<Fuente> todas = repositorio.encontrarTodas();

    assertEquals(2, todas.size());
    assertTrue(todas.contains(f1));
    assertTrue(todas.contains(f2));
  }

  @Test
  void idsIncrementales_alGuardarMultiplesFuentes() {
    Fuente f1 = new Fuente(moduloMock, 1L, LocalDateTime.now());
    Fuente f2 = new Fuente(moduloMock, 2L, LocalDateTime.now());

    repositorio.guardar(f1);
    repositorio.guardar(f2);

    assertEquals(1L, f1.getIdentificador());
    assertEquals(2L, f2.getIdentificador());
  }
}
