package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContribuyenteTest {

  private Contribuyente soloNombre;
  private Contribuyente nombreYApellido;
  private Contribuyente completo;

  @BeforeEach
  void setUp() {
    soloNombre = Contribuyente.crear("Juan");
    nombreYApellido = Contribuyente.crear("Ana", "Gonzalez");
    completo = Contribuyente.crear("Carlos", "Lopez", 35);
  }

  @Test
  void crearSoloConNombre() {
    assertEquals("Juan", soloNombre.getNombre());
    assertNull(soloNombre.getApellido());
    assertNull(soloNombre.getEdad());
    assertNotNull(soloNombre.getHechosCreados());
    assertTrue(soloNombre.getHechosCreados().isEmpty());
  }

  @Test
  void crearConNombreYApellido() {
    assertEquals("Ana", nombreYApellido.getNombre());
    assertEquals("Gonzalez", nombreYApellido.getApellido());
    assertNull(nombreYApellido.getEdad());
    assertNotNull(nombreYApellido.getHechosCreados());
    assertTrue(nombreYApellido.getHechosCreados().isEmpty());
  }

  @Test
  void crearConTodosLosDatos() {
    assertEquals("Carlos", completo.getNombre());
    assertEquals("Lopez", completo.getApellido());
    assertEquals(35, completo.getEdad());
    assertNotNull(completo.getHechosCreados());
    assertTrue(completo.getHechosCreados().isEmpty());
  }

  @Test
  void agregarHechoLoAgregaALaLista() {
    Hecho hecho = new Hecho(); // asumimos constructor sin args
    completo.agregarHecho(hecho);
    assertFalse(completo.getHechosCreados().isEmpty());
    assertEquals(1, completo.getHechosCreados().size());
    assertTrue(completo.getHechosCreados().contains(hecho));
  }
}