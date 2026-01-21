package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdministradorControlador.class)
public class AdministradorControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IAdministradorServicio administradorServicio;

  @Test
  void testCrearAdministrador() throws Exception {
    Administrador adminMock = new Administrador();
    when(administradorServicio.crear(administrador)).thenReturn(adminMock);

    mockMvc.perform(post("/api/administrador/crear"))
            .andExpect(status().isOk());
  }
}
