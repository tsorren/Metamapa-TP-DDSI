package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteDinamicaServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FuenteDinamicaControlador.class)
public class FuenteDinamicaControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IFuenteDinamicaServicio fuenteDinamicaServicio;

  @Test
  void testConsultarFuentes() throws Exception {
    FuenteDinamicaOutputDTO fuente = new FuenteDinamicaOutputDTO(1L, LocalDateTime.now());
    when(fuenteDinamicaServicio.encontrarTodas()).thenReturn(List.of(fuente));

    mockMvc.perform(get("/fuentes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(1L));
  }

  @Test
  void testCargarHecho() {
    IFuenteDinamicaServicio mockServicio = mock(IFuenteDinamicaServicio.class);
    FuenteDinamicaControlador controlador = new FuenteDinamicaControlador(mockServicio);

    HechoInputDTO input = new HechoInputDTO();
    input.setIdContribuyente(1L);
    input.setTitulo("Título");
    input.setDescripcion("Descripción");
    input.setIdCategoria(2L);
    input.setLatitud(0.0);
    input.setLongitud(0.0);
    input.setFechaDelHecho(LocalDate.now());
    input.setIdContenido(3L);

    HechoOutputDTO output = new HechoOutputDTO(
            99L, "Título", "Descripción", null, null, LocalDate.now(),
            LocalDateTime.now(), LocalDateTime.now(), null, null, List.of(), true
    );

    doAnswer(inv -> output).when(mockServicio).cargarHecho(
            1L, "Título", "Descripción", 2L, 0.0, 0.0, input.getFechaDelHecho(), 3L
    );

    HechoOutputDTO resultado = controlador.cargarHecho(input);

    assertNotNull(resultado);
    assertEquals("Título", resultado.getTitulo());
  }

}

