package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UbicacionTest {

  @Test
  @DisplayName("distanciaA devuelve 0 entre ubicaciones idénticas")
  void mismaUbicacionDistanciaCero() {
    Ubicacion u1 = new Ubicacion(40.7128, -74.0060);
    Ubicacion u2 = new Ubicacion(40.7128, -74.0060);
    assertEquals(0.0, u1.distanciaA(u2), 0.01);
  }

  @Test
  @DisplayName("distanciaA devuelve distancia correcta en kilómetros")
  void ubicacionesDiferentesDistanciaCorrecta() {
    Ubicacion buenosAires = new Ubicacion(-34.6037, -58.3816);
    Ubicacion cordoba = new Ubicacion(-31.4167, -64.1833);

    double distancia = buenosAires.distanciaA(cordoba);

    assertTrue(distancia > 600 && distancia < 700, "La distancia debe estar entre 600 y 700 km");
  }

  @Test
  @DisplayName("getters y setters funcionan correctamente")
  void gettersSetters() {
    Ubicacion u = new Ubicacion(0.0, 0.0);
    u.setLatitud(12.34);
    u.setLongitud(56.78);
    assertEquals(12.34, u.getLatitud());
    assertEquals(56.78, u.getLongitud());
  }
}
