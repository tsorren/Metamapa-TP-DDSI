package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteProxyInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteProxyOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteProxyServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuenteProxyControlador.class)
public class FuenteProxyControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IFuenteProxyServicio fuenteProxyService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testCrearFuenteProxy() throws Exception {
    FuenteProxyInputDTO input = new FuenteProxyInputDTO();
    FuenteProxyOutputDTO output = new FuenteProxyOutputDTO(1L, LocalDateTime.now());

    when(fuenteProxyService.crear(any(String.class))).thenReturn(output);

    mockMvc.perform(post("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1L));
  }

  @Test
  void testEncontrarFuentes() throws Exception {
    FuenteProxyOutputDTO fuente = new FuenteProxyOutputDTO(2L, LocalDateTime.now().plusDays(1));
    when(fuenteProxyService.encontrarFuentes()).thenReturn(List.of(fuente));

    mockMvc.perform(get("/fuentes?fechaAnteriorA=" + LocalDateTime.now()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(2L));
  }

  @Test
  void testEncontrarHechosDeFuente() throws Exception {
    HechoOutputDTO hecho = new HechoOutputDTO(
            3L, "Incendio", "Zona norte", null, null,
            LocalDate.now(), LocalDateTime.now(), null, null, Set.of(), true
    );

    when(fuenteProxyService.encontrarHechos(3L)).thenReturn(List.of(hecho));

    mockMvc.perform(get("/fuentes/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(3L));
  }

  @Test
  void testEncontrarHechoPorId() throws Exception {
    HechoOutputDTO hecho = new HechoOutputDTO(
            10L, "Inundación", "Río desbordado", null, null,
            LocalDate.now(), LocalDateTime.now(), null, null, Set.of(), true
    );

    when(fuenteProxyService.encontrarHechoPorId(5L, 10L)).thenReturn(hecho);

    mockMvc.perform(get("/fuentes/5/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(10L));
  }
}
