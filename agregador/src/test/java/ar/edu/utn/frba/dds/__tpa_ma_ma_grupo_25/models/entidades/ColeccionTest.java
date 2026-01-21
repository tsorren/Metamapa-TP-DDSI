package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColeccionTest {

  @Test
  public void seCreaColeccionConTodosLosDatos() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    List<Fuente> fuentes = new ArrayList<>();

    Coleccion coleccion = Coleccion.crear("Eventos climáticos", "Colección de hechos meteorológicos", criterio, fuentes);

    assertEquals("Eventos climáticos", coleccion.getTitulo());
    assertEquals("Colección de hechos meteorológicos", coleccion.getDescripcion());
    assertEquals(criterio, coleccion.getCriterioDePertenencia());
    assertEquals(fuentes, coleccion.getFuentes());
  }

  @Test
  public void sePuedeAgregarFuenteALaColeccion() {
    Coleccion coleccion = Coleccion.crear("Eventos", "Descripción", new CriterioDePertenencia(), new ArrayList<>());
    Fuente fuenteMock = new Fuente(); // Fuente puede ser una clase abstracta o concreta; ajustá según tu modelo

    coleccion.agregarFuente(fuenteMock);

    assertEquals(1, coleccion.getFuentes().size());
    assertTrue(coleccion.getFuentes().contains(fuenteMock));
  }

  @Test
  public void builderFuncionaCorrectamente() {
    Coleccion coleccion = Coleccion.builder()
            .titulo("Sociales")
            .descripcion("Hechos de interés social")
            .fuentes(new ArrayList<>())
            .criterioDePertenencia(new CriterioDePertenencia())
            .build();

    assertEquals("Sociales", coleccion.getTitulo());
    assertEquals("Hechos de interés social", coleccion.getDescripcion());
    assertNotNull(coleccion.getFuentes());
    assertNotNull(coleccion.getCriterioDePertenencia());
  }
}
