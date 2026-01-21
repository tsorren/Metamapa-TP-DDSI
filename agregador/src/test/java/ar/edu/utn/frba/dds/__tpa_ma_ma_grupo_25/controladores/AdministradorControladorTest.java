package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.AdministradorControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdministradorControlador.class)
public class AdministradorControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IAdministradorServicio administradorServicio;

  @Test
  void testCrearAdministrador() throws Exception {
    AdministradorOutputDTO dto = new AdministradorOutputDTO(1L);

    when(administradorServicio.crear()).thenReturn(dto);

    mockMvc.perform(post("/administradores")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1L));
  }
}
