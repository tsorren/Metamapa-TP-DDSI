package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.ContribuyenteServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ContribuyenteServicioTest {

  private IContribuyenteRepositorio contribuyenteRepositorio;
  private ContribuyenteServicio contribuyenteServicio;

  @BeforeEach
  void setUp() {
    contribuyenteRepositorio = mock(IContribuyenteRepositorio.class);
    contribuyenteServicio = new ContribuyenteServicio(contribuyenteRepositorio);
  }

  @Test
  void testCrearContribuyente() {
    // Arrange
    String nombre = "Juan";
    String apellido = "Pérez";
    Integer edad = 30;

    // Act
    Contribuyente contribuyente = contribuyenteServicio.crear(nombre, apellido, edad);

    // Assert
    assertNotNull(contribuyente);
    assertEquals(nombre, contribuyente.getNombre());
    assertEquals(apellido, contribuyente.getApellido());
    assertEquals(edad, contribuyente.getEdad());

    verify(contribuyenteRepositorio, times(1)).guardar(contribuyente);
  }
}
