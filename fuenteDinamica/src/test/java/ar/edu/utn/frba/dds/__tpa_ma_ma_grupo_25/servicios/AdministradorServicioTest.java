package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.AdministradorServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdministradorServicioTest {

  private IAdministradorRepositorio administradorRepositorio;
  private AdministradorServicio administradorServicio;

  @BeforeEach
  void setUp() {
    administradorRepositorio = mock(IAdministradorRepositorio.class);
    administradorServicio = new AdministradorServicio(administradorRepositorio);
  }

  @Test
  void testCrearAdministrador() {
    // Act
    Administrador administrador = administradorServicio.crear(administrador);

    // Assert
    assertNotNull(administrador);
    verify(administradorRepositorio, times(1)).guardar(administrador);
  }
}
