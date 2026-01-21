package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdministradorTest {

  @Test
  public void sePuedeCrearAdministradorYAsignarIdentificador() {
    Administrador admin = new Administrador();
    admin.setIdentificador(1L);

    assertNotNull(admin);
    assertEquals(1L, admin.getIdentificador());
  }
}
