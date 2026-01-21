package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.FiltroControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FiltroInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.Filtro;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.FiltroDescripcion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFiltroServicio;
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
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FiltroControlador.class)
public class FiltroControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IFiltroServicio filtroServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testEncontrarTodos() throws Exception {
    Filtro filtro1 = new FiltroDescripcion("Incendio");
    filtro1.setIdentificador(1L);
    Filtro filtro2 = new FiltroDescripcion("Inundación");
    filtro2.setIdentificador(2L);

    Mockito.when(filtroServicio.encontrarTodos()).thenReturn(List.of(filtro1, filtro2));

    mockMvc.perform(get("/filtros"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].tipo", is("Descripcion")))
            .andExpect(jsonPath("$[0].descripcion", is("Incendio")))
            .andExpect(jsonPath("$[1].descripcion", is("Inundación")));
  }

  @Test
  void testCrearFiltro() throws Exception {
    FiltroInputDTO inputDTO = new FiltroInputDTO(1L);

    Filtro filtroCreado = new FiltroDescripcion("Emergencia");
    filtroCreado.setIdentificador(1L);

    Mockito.when(filtroServicio.crear(any(FiltroInputDTO.class))).thenReturn(filtroCreado);

    mockMvc.perform(post("/filtros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipo", is("Descripcion")))
            .andExpect(jsonPath("$.descripcion", is("Emergencia")));
  }
}
