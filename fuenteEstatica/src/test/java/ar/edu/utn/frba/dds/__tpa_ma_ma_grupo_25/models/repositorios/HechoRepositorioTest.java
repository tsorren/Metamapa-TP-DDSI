package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Etiqueta;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.HechoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HechoRepositorioTest {

  private HechoRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new HechoRepositorio();
  }

  @Test
  void guardaUnHechoYLeAsignaUnId() {
    double latitud = -34.6037;
    double longitud = -58.3816;
    Hecho hecho = Hecho.crear("Robo en el centro", "Robo a mano armada", new Categoria("algo"), new Ubicacion(latitud, longitud), LocalDate.of(2024, 6, 10), TipoOrigen.DATASET);

    assertNull(hecho.getIdentificador());

    repositorio.guardar(hecho);

    assertNotNull(hecho.getIdentificador());
    assertEquals(hecho, repositorio.encontrarPorId(hecho.getIdentificador()));
  }

  @Test
  void guardaMultiplesHechosYLosRecuperaTodos() {
    double latitud = -34.6037;
    double longitud = -58.3816;
    Hecho h1 = Hecho.crear("Título 1", "Desc 1", new Categoria("algo"), new Ubicacion(latitud, longitud), LocalDate.now(), TipoOrigen.DATASET);
    Hecho h2 = Hecho.crear("Título 2", "Desc 2", new Categoria("algo"), new Ubicacion(latitud, longitud), LocalDate.now(), TipoOrigen.DATASET);

    repositorio.guardar(h1);
    repositorio.guardar(h2);

    List<Hecho> todos = repositorio.encontrarTodos();

    assertEquals(2, todos.size());
    assertTrue(todos.contains(h1));
    assertTrue(todos.contains(h2));
  }

  @Test
  void recuperarHechoPorIdInexistenteDevuelveNull() {
    assertNull(repositorio.encontrarPorId(999L));
  }

  @Test
  void hechoInicialmenteEsVisibleYPermiteOcultarse() {
    double latitud = -34.6037;
    double longitud = -58.3816;
    Hecho hecho = Hecho.crear("Fuga de gas", "Explosión", new Categoria("algo"), new Ubicacion(latitud, longitud), LocalDate.now(), TipoOrigen.DATASET);
    assertTrue(hecho.getVisible());

    hecho.ocultar();

    assertFalse(hecho.getVisible());
  }

  @Test
  void agregarEtiquetaAUnHecho() {
    double latitud = -34.6037;
    double longitud = -58.3816;
    Hecho hecho = Hecho.crear("Accidente", "Choque múltiple", new Categoria("accidente"), new Ubicacion(latitud, longitud), LocalDate.now(), TipoOrigen.DATASET);
    Etiqueta etiqueta = new Etiqueta("Urgente");

    hecho.agregarEtiqueta(etiqueta);

    assertNotNull(hecho.getEtiquetas());
    assertTrue(hecho.getEtiquetas().contains(etiqueta));
  }
}
