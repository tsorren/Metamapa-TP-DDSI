package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuenteDinamicaTest {

  private FuenteDinamica fuenteDinamica;

  @BeforeEach
  void setUp() {
    fuenteDinamica = new FuenteDinamica();
  }

  @Test
  void alCrearFuenteDinamicaLaListaDeHechosEstaVacia() {
    assertNotNull(fuenteDinamica.getHechosAsociados(), "La lista de hechos no debería ser nula");
    assertTrue(fuenteDinamica.getHechosAsociados().isEmpty(), "La lista de hechos debería estar vacía");
  }

  @Test
  void alCrearFuenteDinamicaLaFechaDeCreacionEsAsignada() {
    LocalDateTime ahora = LocalDateTime.now();
    assertNotNull(fuenteDinamica.getFechaDeCreacion(), "La fecha de creación no debe ser nula");
    assertFalse(fuenteDinamica.getFechaDeCreacion().isAfter(ahora), "La fecha de creación no debe ser futura");
  }

  @Test
  void puedoAsignarIdentificador() {
    fuenteDinamica.setIdentificador(123L);
    assertEquals(123L, fuenteDinamica.getIdentificador());
  }

  @Test
  void puedoAgregarUnHechoALaLista() {
    fuenteDinamica.getHechosAsociados().add(101L);
    assertEquals(1, fuenteDinamica.getHechosAsociados().size());
    assertTrue(fuenteDinamica.getHechosAsociados().contains(101L));
  }

  @Test
  void puedoAgregarMultiplesHechosALaLista() {
    fuenteDinamica.getHechosAsociados().addAll(List.of(1L, 2L, 3L));
    assertEquals(3, fuenteDinamica.getHechosAsociados().size());
    assertTrue(fuenteDinamica.getHechosAsociados().containsAll(List.of(1L, 2L, 3L)));
  }

}
