package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.FuenteProxyRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuenteProxyRepositorioTest {

  private FuenteProxyRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new FuenteProxyRepositorio();
  }

  @Test
  void guardaUnaFuenteProxyYLeAsignaId() {
    FuenteProxyStub fuente = new FuenteProxyStub();
    assertNull(fuente.getIdentificador());

    repositorio.guardar(fuente);

    assertNotNull(fuente.getIdentificador());
    assertEquals(fuente, repositorio.encontrarPorId(fuente.getIdentificador()));
  }

  @Test
  void guardaMultiplesFuentesProxyYLasRecuperaTodas() {
    FuenteProxyStub f1 = new FuenteProxyStub();
    FuenteProxyStub f2 = new FuenteProxyStub();

    repositorio.guardar(f1);
    repositorio.guardar(f2);

    List<FuenteProxyStub> todas = repositorio.encontrarTodas().stream()
            .map(fp -> (FuenteProxyStub) fp)
            .toList();

    assertEquals(2, todas.size());
    assertTrue(todas.contains(f1));
    assertTrue(todas.contains(f2));
  }

  @Test
  void buscarFuenteProxyPorIdInexistenteDevuelveNull() {
    assertNull(repositorio.encontrarPorId(999L));
  }
}
