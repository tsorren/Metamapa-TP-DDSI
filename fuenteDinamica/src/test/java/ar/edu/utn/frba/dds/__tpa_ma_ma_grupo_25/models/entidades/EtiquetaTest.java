package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EtiquetaTest {

  @Test
  @DisplayName("constructor asigna nombre correctamente")
  void creaEtiquetaConNombre() {
    Etiqueta etiqueta = new Etiqueta("urgente");
    assertEquals("urgente", etiqueta.getNombre());
  }

  @Test
  @DisplayName("puede establecer identificador")
  void setIdentificador() {
    Etiqueta etiqueta = new Etiqueta("evento");
    etiqueta.setIdentificador(42L);
    assertEquals(42L, etiqueta.getIdentificador());
  }
}
