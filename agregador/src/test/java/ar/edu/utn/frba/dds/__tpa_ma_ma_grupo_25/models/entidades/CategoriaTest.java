package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CategoriaTest {

  @Test
  public void seCreaCategoriaConNombre() {
    Categoria categoria = new Categoria("Accidentes");

    assertEquals("Accidentes", categoria.getNombre());
    assertNull(categoria.getIdentificador());
  }

  @Test
  public void sePuedeAsignarIdentificador() {
    Categoria categoria = new Categoria("Incendios");
    categoria.setIdentificador(5L);

    assertEquals(5L, categoria.getIdentificador());
  }
}
