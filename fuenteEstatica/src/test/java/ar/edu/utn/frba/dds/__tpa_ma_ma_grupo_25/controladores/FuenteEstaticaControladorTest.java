package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteEstaticaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteEstaticaServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuenteEstaticaControlador.class)
public class FuenteEstaticaControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IFuenteEstaticaServicio servicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testCrearFuente() throws Exception {
    FuenteEstaticaInputDTO input = new FuenteEstaticaInputDTO(LocalDateTime.now(), "/ruta/fuente.csv");
    FuenteOutputDTO output = new FuenteOutputDTO(1L, input.getFechaCreacio());

    when(servicio.crearFuente(any(Path.class))).thenReturn(output);

    mockMvc.perform(post("/fuentes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1L));
  }

  @Test
  void testConsultarFuentes() throws Exception {
    FuenteOutputDTO fuente = new FuenteOutputDTO(2L, LocalDateTime.now());
    when(servicio.encontrarTodas()).thenReturn(List.of(fuente));

    mockMvc.perform(get("/fuentes?fechaAnteriorA=" + LocalDateTime.now().minusDays(1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(2L));
  }

  @Test
  void testImportarHechos() throws Exception {
    HechoOutputDTO hecho = new HechoOutputDTO(
            5L, "Incendio", "Gran incendio", null, null,
            LocalDate.now(), LocalDateTime.now(), LocalDateTime.now(),
            null, null, List.of(), true
    );

    when(servicio.importarHechos(5L)).thenReturn(List.of(hecho));

    mockMvc.perform(get("/fuentes/importar/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(5L));
  }

  @Test
  void testHechosDeFuente() throws Exception {
    HechoOutputDTO hecho = new HechoOutputDTO(
            3L, "Inundación", "Zona centro", null, null,
            LocalDate.now(), LocalDateTime.now(), LocalDateTime.now(),
            null, null, List.of(), true
    );

    when(servicio.hechosDeFuente(3L)).thenReturn(List.of(hecho));

    mockMvc.perform(get("/fuentes/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(3L));
  }
}
