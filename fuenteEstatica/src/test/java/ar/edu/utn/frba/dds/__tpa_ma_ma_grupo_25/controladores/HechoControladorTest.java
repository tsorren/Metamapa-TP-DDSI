package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HechoControlador.class)
public class HechoControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IHechoServicio hechoServicio;

  @Test
  void testOcultarHecho() throws Exception {
    doNothing().when(hechoServicio).ocultarHecho(10L);

    mockMvc.perform(patch("/ocultar/10"))
            .andExpect(status().isOk());
  }
}
