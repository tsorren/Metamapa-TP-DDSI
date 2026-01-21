package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ColeccionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColeccionRepositorioTest {

  private ColeccionRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new ColeccionRepositorio();
  }

  private Coleccion crearColeccionDePrueba() {
    ModuloFuente modulo = new ModuloFuente(TipoFuente.ESTATICA, "http://api.com", WebClient.create());
    Fuente fuente = new Fuente(modulo, 10L, LocalDateTime.now());
    CriterioDePertenencia criterioMock = new CriterioDePertenencia(); // puede estar vacío

    return Coleccion.crear("Título de prueba", "Descripción de prueba", criterioMock, List.of(fuente));
  }

  @Test
  void guardar_coleccionSinIdYHandle_losGeneraCorrectamente() {
    Coleccion coleccion = crearColeccionDePrueba();

    repositorio.guardar(coleccion);

    assertNotNull(coleccion.getIdentificador());
    assertNotNull(coleccion.getHandle());
    assertEquals(1L, coleccion.getIdentificador());
    assertFalse(coleccion.getHandle().isEmpty());
  }

  @Test
  void guardar_coleccionConIdYHandle_losPreserva() {
    Coleccion coleccion = crearColeccionDePrueba();
    coleccion.setIdentificador(99L);
    coleccion.setHandle("handle-especifico");

    repositorio.save(coleccion);

    Coleccion recuperada = repositorio.encontrarPorId(99L);
    assertEquals("handle-especifico", recuperada.getHandle());
    assertEquals(99L, recuperada.getIdentificador());
  }

  @Test
  void encontrarPorId_existente_devuelveCorrecto() {
    Coleccion coleccion = crearColeccionDePrueba();
    repositorio.guardar(coleccion);

    Coleccion encontrada = repositorio.encontrarPorId(coleccion.getIdentificador());

    assertNotNull(encontrada);
    assertEquals(coleccion, encontrada);
  }

  @Test
  void encontrarPorId_inexistente_devuelveNull() {
    assertNull(repositorio.encontrarPorId(123456L));
  }

  @Test
  void encontrarTodas_retornaTodasLasColecciones() {
    Coleccion coleccion1 = crearColeccionDePrueba();
    Coleccion coleccion2 = crearColeccionDePrueba();

    repositorio.guardar(coleccion1);
    repositorio.guardar(coleccion2);

    List<Coleccion> todas = repositorio.encontrarTodas();

    assertEquals(2, todas.size());
    assertTrue(todas.contains(coleccion1));
    assertTrue(todas.contains(coleccion2));
  }

  @Test
  void guardar_multiplesColecciones_idsIncrementan() {
    Coleccion col1 = crearColeccionDePrueba();
    Coleccion col2 = crearColeccionDePrueba();
    Coleccion col3 = crearColeccionDePrueba();

    repositorio.guardar(col1);
    repositorio.guardar(col2);
    repositorio.guardar(col3);

    assertEquals(1L, col1.getIdentificador());
    assertEquals(2L, col2.getIdentificador());
    assertEquals(3L, col3.getIdentificador());
  }

  @Test
  void agregarFuente_agregaCorrectamenteALaColeccion() {
    Coleccion coleccion = crearColeccionDePrueba();

    Fuente nuevaFuente = new Fuente(
            new ModuloFuente(TipoFuente.DINAMICA, "http://otra.api", WebClient.create()),
            20L,
            LocalDateTime.now()
    );

    coleccion.agregarFuente(nuevaFuente);

    assertTrue(coleccion.getFuentes().contains(nuevaFuente));
  }
}
