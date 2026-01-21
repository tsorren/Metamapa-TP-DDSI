package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica.ContribuyenteControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContribuyenteControlador.class)
public class ContribuyenteControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IContribuyenteServicio contribuyenteServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testCrearContribuyente() throws Exception {
    ContribuyenteInputDTO inputDTO = new ContribuyenteInputDTO();
    Contribuyente contribuyente = Contribuyente.crear("Juan", "Pérez", 30);
    contribuyente.setIdentificador(100L);

    Mockito.when(contribuyenteServicio.crear(eq("Juan"), eq("Pérez"), eq(30)))
            .thenReturn(contribuyente);

    mockMvc.perform(post("/contribuyentes/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan"))
            .andExpect(jsonPath("$.apellido").value("Pérez"))
            .andExpect(jsonPath("$.edad").value(30));
  }
}
