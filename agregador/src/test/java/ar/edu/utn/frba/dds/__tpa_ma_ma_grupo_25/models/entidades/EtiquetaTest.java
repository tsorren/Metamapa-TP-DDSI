package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Etiqueta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EtiquetaTest {

  @Test
  public void seCreaEtiquetaConNombre() {
    Etiqueta etiqueta = new Etiqueta("urgente");

    assertEquals("urgente", etiqueta.getNombre());
    assertNull(etiqueta.getIdentificador());
  }

  @Test
  public void sePuedeAsignarIdentificador() {
    Etiqueta etiqueta = new Etiqueta("relevante");
    etiqueta.setIdentificador(3L);

    assertEquals(3L, etiqueta.getIdentificador());
  }
}
