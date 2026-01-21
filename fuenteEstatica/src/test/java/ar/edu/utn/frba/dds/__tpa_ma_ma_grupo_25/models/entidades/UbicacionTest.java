package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UbicacionTest {

  @Test
  public void testDistanciaEntreUbicacionesConocidas() {
    Ubicacion obelisco = new Ubicacion(-34.6037, -58.3816); // Buenos Aires
    Ubicacion catedralCordoba = new Ubicacion(-31.4167, -64.1833); // Córdoba Capital

    double distancia = obelisco.distanciaA(catedralCordoba);

    assertEquals(647, Math.round(distancia)); // Aproximadamente 647 km
  }

  @Test
  public void testDistanciaCero() {
    Ubicacion lugar = new Ubicacion(-34.6037, -58.3816);
    assertEquals(0.0, lugar.distanciaA(lugar));
  }
}
