package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Etiqueta;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.EtiquetaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EtiquetaRepositorioTest {

  private EtiquetaRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new EtiquetaRepositorio();
  }

  @Test
  void guardar_asignaIdentificadorSiEsNull() {
    Etiqueta etiqueta = new Etiqueta("test");
    etiqueta.setIdentificador(null);

    repositorio.guardar(etiqueta);

    assertNotNull(etiqueta.getIdentificador());
    assertEquals(1L, etiqueta.getIdentificador());
  }

  @Test
  void guardar_noModificaIdentificadorSiNoEsNull() {
    Etiqueta etiqueta = new Etiqueta("test");
    etiqueta.setIdentificador(100L);

    repositorio.guardar(etiqueta);

    assertEquals(100L, etiqueta.getIdentificador());
  }

  @Test
  void encontrarPorId_existente_devuelveEtiqueta() {
    Etiqueta etiqueta = new Etiqueta("test");
    repositorio.guardar(etiqueta);
    Long id = etiqueta.getIdentificador();

    Etiqueta resultado = repositorio.encontrarPorId(id);

    assertNotNull(resultado);
    assertEquals(etiqueta, resultado);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    Etiqueta resultado = repositorio.encontrarPorId(999L);
    assertNull(resultado);
  }

  @Test
  void encontrarTodas_devuelveTodasLasEtiquetasGuardadas() {
    Etiqueta e1 = new Etiqueta("tag1");
    Etiqueta e2 = new Etiqueta("tag2");
    repositorio.guardar(e1);
    repositorio.guardar(e2);

    List<Etiqueta> todas = repositorio.encontrarTodas();

    assertEquals(2, todas.size());
    assertTrue(todas.contains(e1));
    assertTrue(todas.contains(e2));
  }

  @Test
  void idsIncrementales_alGuardarMultiplesEtiquetas() {
    Etiqueta e1 = new Etiqueta("tag1");
    Etiqueta e2 = new Etiqueta("tag2");

    repositorio.guardar(e1);
    repositorio.guardar(e2);

    assertEquals(1L, e1.getIdentificador());
    assertEquals(2L, e2.getIdentificador());
  }
}
