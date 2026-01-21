package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.HechoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HechoRepositorioTest {

  private HechoRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new HechoRepositorio();
  }

  private Hecho crearHechoDePrueba(Long idExterno, String titulo) {
    return Hecho.crear(
            idExterno,
            titulo,
            "Descripcion prueba",
            new Categoria("Test"),
            new Ubicacion(-34.6, -58.4),
            LocalDate.of(2023, 6, 19),
            LocalDateTime.now(),
            LocalDateTime.now(),
            new Multimedia(1L, TipoContenido.AUDIO, "url"),
            TipoOrigen.EXTERNO,
            List.of(new Etiqueta("tag1")),
            true
    );
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    Hecho hecho = crearHechoDePrueba(10L, "Titulo1");
    hecho.setIdentificador(null);

    repositorio.guardar(hecho);

    assertNotNull(hecho.getIdentificador());
    assertEquals(1L, hecho.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    Hecho hecho = crearHechoDePrueba(10L, "Titulo2");
    hecho.setIdentificador(50L);

    repositorio.guardar(hecho);

    assertEquals(50L, hecho.getIdentificador());
  }

  @Test
  void encontrarPorId_devuelveHechoCorrecto() {
    Hecho hecho = crearHechoDePrueba(11L, "Titulo3");
    repositorio.guardar(hecho);
    Long id = hecho.getIdentificador();

    Hecho resultado = repositorio.encontrarPorId(id);

    assertNotNull(resultado);
    assertEquals(hecho, resultado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    Hecho resultado = repositorio.encontrarPorId(999L);
    assertNull(resultado);
  }

  @Test
  void encontrarTodos_devuelveTodosLosHechos() {
    Hecho hecho1 = crearHechoDePrueba(1L, "TituloA");
    Hecho hecho2 = crearHechoDePrueba(2L, "TituloB");

    repositorio.guardar(hecho1);
    repositorio.guardar(hecho2);

    List<Hecho> todos = repositorio.encontrarTodos();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(hecho1));
    assertTrue(todos.contains(hecho2));
  }

  @Test
  void idsIncrementales_alGuardarMultiplesHechos() {
    Hecho hecho1 = crearHechoDePrueba(100L, "TituloX");
    Hecho hecho2 = crearHechoDePrueba(101L, "TituloY");

    repositorio.guardar(hecho1);
    repositorio.guardar(hecho2);

    assertEquals(1L, hecho1.getIdentificador());
    assertEquals(2L, hecho2.getIdentificador());
  }
}
