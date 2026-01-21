package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.EtiquetaControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.EtiquetaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EtiquetaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEtiquetaServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EtiquetaControlador.class)
public class EtiquetaControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IEtiquetaServicio etiquetaServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testCrearEtiqueta() throws Exception {
    EtiquetaInputDTO inputDTO = new EtiquetaInputDTO(1L, "Emergencia");
    EtiquetaOutputDTO outputDTO = new EtiquetaOutputDTO(1L, "Emergencia");

    Mockito.when(etiquetaServicio.crear("Emergencia")).thenReturn(outputDTO);

    mockMvc.perform(post("/etiquetas/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Emergencia")));
  }

  @Test
  void testEncontrarTodas() throws Exception {
    List<EtiquetaOutputDTO> etiquetas = List.of(
            new EtiquetaOutputDTO(1L, "Emergencia"),
            new EtiquetaOutputDTO(2L, "Urgente")
    );

    Mockito.when(etiquetaServicio.encontrarTodas()).thenReturn(etiquetas);

    mockMvc.perform(get("/etiquetas/todas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nombre", is("Emergencia")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nombre", is("Urgente")));
  }

  @Test
  void testEncontrarPorId() throws Exception {
    EtiquetaOutputDTO outputDTO = new EtiquetaOutputDTO(1L, "Urgente");

    Mockito.when(etiquetaServicio.encontrarPorId(1L)).thenReturn(outputDTO);

    mockMvc.perform(get("/etiquetas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Urgente")));
  }
}
