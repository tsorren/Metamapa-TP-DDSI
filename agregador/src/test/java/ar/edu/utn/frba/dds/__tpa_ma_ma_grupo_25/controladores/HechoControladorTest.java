package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica.PublicHechoControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicHechoControlador.class)
public class HechoControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IHechoServicio hechoServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testEncontrarTodos() throws Exception {
    Categoria categoria = new Categoria("Incendio");

    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);

    Multimedia multimedia = new Multimedia(1L, TipoContenido.IMAGEN, "http://url.imagen");

    FuenteOutputDTO fuente = new FuenteOutputDTO(1L, 100L, null);

    HechoOutputDTO hechoOutput = new HechoOutputDTO(
            1L,
            100L,
            fuente,
            "Titulo hecho",
            "Descripcion hecho",
            categoria,
            ubicacion,
            LocalDate.of(2023, 1, 1),
            LocalDateTime.of(2023, 1, 2, 12, 0),
            LocalDateTime.of(2023, 1, 3, 12, 0),
            multimedia,
            TipoOrigen.MANUAL,
            List.of(new Etiqueta("Etiqueta1")),
            true
    );

    List<HechoOutputDTO> hechos = List.of(hechoOutput);

    // Mock del servicio: cuando se llame encontrarTodos con cualquier DTO, devuelve lista mockeada
    Mockito.when(hechoServicio.encontrarTodos(any(HechosFiltrandoInputDTO.class))).thenReturn(hechos);

    HechosFiltrandoInputDTO filtroDTO = new HechosFiltrandoInputDTO(null, LocalDateTime.now(), LocalDateTime.now(), LocalDate.now(), LocalDate.now(), null);

    mockMvc.perform(get("/hechos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(filtroDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].titulo", is("Titulo hecho")))
            .andExpect(jsonPath("$[0].categoria.nombre", is("Incendio")))
            .andExpect(jsonPath("$[0].fuente.identificador", is(1)))
            .andExpect(jsonPath("$[0].etiquetas[0].nombre", is("Etiqueta1")));
  }

  @Test
  void testEncontrarPorId() throws Exception {
    Categoria categoria = new Categoria("Incendio");

    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);

    Multimedia multimedia = new Multimedia(1L, TipoContenido.IMAGEN, "http://url.imagen");

    FuenteOutputDTO fuente = new FuenteOutputDTO(1L, 100L, null);

    HechoOutputDTO hechoOutput = new HechoOutputDTO(
            1L,
            100L,
            fuente,
            "Titulo hecho",
            "Descripcion hecho",
            categoria,
            ubicacion,
            LocalDate.of(2023, 1, 1),
            LocalDateTime.of(2023, 1, 2, 12, 0),
            LocalDateTime.of(2023, 1, 3, 12, 0),
            multimedia,
            TipoOrigen.MANUAL,
            List.of(new Etiqueta("Etiqueta1")),
            true
    );

    Mockito.when(hechoServicio.encontrarPorId(1L)).thenReturn(hechoOutput);

    mockMvc.perform(get("/hechos/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo", is("Titulo hecho")))
            .andExpect(jsonPath("$.categoria.nombre", is("Incendio")))
            .andExpect(jsonPath("$.fuente.identificador", is(1)))
            .andExpect(jsonPath("$.etiquetas[0].nombre", is("Etiqueta1")));
  }

  @Test
  void testEtiquetar() throws Exception {
    Categoria categoria = new Categoria("Incendio");

    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);

    Multimedia multimedia = new Multimedia(1L, TipoContenido.IMAGEN, "http://url.imagen");

    FuenteOutputDTO fuente = new FuenteOutputDTO(1L, 100L, null);

    HechoOutputDTO hechoOutput = new HechoOutputDTO(
            1L,
            100L,
            fuente,
            "Titulo hecho",
            "Descripcion hecho",
            categoria,
            ubicacion,
            LocalDate.of(2023, 1, 1),
            LocalDateTime.of(2023, 1, 2, 12, 0),
            LocalDateTime.of(2023, 1, 3, 12, 0),
            multimedia,
            TipoOrigen.MANUAL,
            List.of(new Etiqueta("Etiqueta1"), new Etiqueta("EtiquetaNueva")),
            true
    );

    Mockito.when(hechoServicio.etiquetar(1L, 2L)).thenReturn(hechoOutput);

    mockMvc.perform(post("/hechos/etiquetar")
                    .param("idHecho", "1")
                    .param("idEtiqueta", "2")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.etiquetas", hasSize(2)))
            .andExpect(jsonPath("$.etiquetas[1].nombre", is("EtiquetaNueva")));
  }
}
