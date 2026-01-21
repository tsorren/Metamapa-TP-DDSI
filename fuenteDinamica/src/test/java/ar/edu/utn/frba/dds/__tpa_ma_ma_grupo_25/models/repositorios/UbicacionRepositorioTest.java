package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.UbicacionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Tests de UbicacionRepositorio")
class UbicacionRepositorioTest {

  private UbicacionRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new UbicacionRepositorio();
  }

  @Test
  @DisplayName("Guarda una ubicación con identificador autogenerado")
  void guardaUbicacion() {
    Ubicacion ubicacion = new Ubicacion(-34.6, -58.4);
    repositorio.guardar(ubicacion);
    assertNotNull(ubicacion.getIdentificador());
  }

  @Test
  @DisplayName("Encuentra una ubicación por su identificador")
  void encuentraPorId() {
    Ubicacion ubicacion = new Ubicacion(-31.4, -64.2);
    repositorio.guardar(ubicacion);
    Ubicacion encontrada = repositorio.encontrarPorId(ubicacion.getIdentificador());
    assertEquals(ubicacion, encontrada);
  }

  @Test
  @DisplayName("Obtiene todas las ubicaciones guardadas")
  void encuentraTodos() {
    repositorio.guardar(new Ubicacion(-32.9, -60.6));
    repositorio.guardar(new Ubicacion(-32.9, -68.8));
    List<Ubicacion> todas = repositorio.encontrarTodos();
    assertEquals(2, todas.size());
  }
}
