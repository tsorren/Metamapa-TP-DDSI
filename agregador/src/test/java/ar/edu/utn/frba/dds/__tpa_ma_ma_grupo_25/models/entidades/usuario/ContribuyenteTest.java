package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ContribuyenteTest {

  @Test
  public void seCreaConNombreSolo() {
    Contribuyente c = Contribuyente.crear("Ana");

    assertEquals("Ana", c.getNombre());
    assertNull(c.getApellido());
    assertNull(c.getEdad());
  }

  @Test
  public void seCreaConNombreYApellido() {
    Contribuyente c = Contribuyente.crear("Luis", "Pérez");

    assertEquals("Luis", c.getNombre());
    assertEquals("Pérez", c.getApellido());
    assertNull(c.getEdad());
  }

  @Test
  public void seCreaConNombreApellidoYEdad() {
    Contribuyente c = Contribuyente.crear("Sofía", "Gómez", 29);

    assertEquals("Sofía", c.getNombre());
    assertEquals("Gómez", c.getApellido());
    assertEquals(29, c.getEdad());
  }

  @Test
  public void permiteModificarDatos() {
    Contribuyente c = Contribuyente.crear("Sofía", "Gómez", 29);

    c.setNombre("Sofi");
    c.setEdad(30);

    assertEquals("Sofi", c.getNombre());
    assertEquals(30, c.getEdad());
  }
}
