package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica.PublicColeccionControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ColeccionFiltradaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ColeccionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IColeccionServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicColeccionControlador.class)
class ColeccionControladorTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  private IColeccionServicio coleccionServicio;

  @Test
  void testCrearColeccion() throws Exception {
    ColeccionInputDTO input = new ColeccionInputDTO("Colección 1", "Descripción", 1L, List.of(10L, 20L));

    FiltroOutputDTO filtro = new FiltroOutputDTO(1L, "porCategoria", List.of("2"));
    CriterioDePertenenciaOutputDTO criterio = new CriterioDePertenenciaOutputDTO(List.of(filtro));

    ColeccionOutputDTO output = new ColeccionOutputDTO(
            1L,
            "handle-coleccion",
            "Colección 1",
            "Descripción",
            emptyList(),
            criterio
    );

    when(coleccionServicio.crear(input)).thenReturn(output);

    mockMvc.perform(post("/colecciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1L))
            .andExpect(jsonPath("$.titulo").value("Colección 1"))
            .andExpect(jsonPath("$.descripcion").value("Descripción"))
            .andExpect(jsonPath("$.criterioDePertenencia.filtros[0].tipoFiltro").value("porCategoria"))
            .andExpect(jsonPath("$.criterioDePertenencia.filtros[0].parametros[0]").value("2"));
  }

  @Test
  void testConsultarColeccion() throws Exception {
    ModuloFuenteOutputDTO modulo = new ModuloFuenteOutputDTO(100L, "http://fuente", TipoFuente.ESTATICA);
    FuenteOutputDTO fuente = new FuenteOutputDTO(50L, 8888L, modulo);
    HechoOutputDTO hecho = new HechoOutputDTO(
            101L,
            9999L,
            fuente,
            "Título Hecho",
            "Descripción Hecho",
            null,
            null,
            LocalDate.now(),
            LocalDateTime.now(),
            null,
            null,
            TipoOrigen.CONTRIBUYENTE,
            List.of(),
            true
    );

    when(coleccionServicio.consultarColeccion(new NavegacionInputDTO())).thenReturn(List.of(hecho));

    mockMvc.perform(get("/colecciones/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(101L))
            .andExpect(jsonPath("$[0].titulo").value("Título Hecho"))
            .andExpect(jsonPath("$[0].fuente.identificador").value(50L))
            .andExpect(jsonPath("$[0].fuente.moduloFuente.url").value("http://fuente"));
  }

  @Test
  void testConsultarColeccionFiltrando() throws Exception {
    ColeccionFiltradaInputDTO input = new ColeccionFiltradaInputDTO();

    ModuloFuenteOutputDTO modulo = new ModuloFuenteOutputDTO(200L, "http://modulo2", TipoFuente.DINAMICA);
    FuenteOutputDTO fuente = new FuenteOutputDTO(60L, 7777L, modulo);
    HechoOutputDTO hechoFiltrado = new HechoOutputDTO(
            202L,
            1111L,
            fuente,
            "Hecho Filtrado",
            "Filtrado correctamente",
            null,
            null,
            LocalDate.now(),
            LocalDateTime.now(),
            null,
            null,
            TipoOrigen.CONTRIBUYENTE,
            List.of(),
            true
    );

    when(coleccionServicio.consultarColeccionFiltrando(1L, 2L)).thenReturn(List.of(hechoFiltrado));

    mockMvc.perform(post("/colecciones/consultarFiltrando")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificador").value(202L))
            .andExpect(jsonPath("$[0].titulo").value("Hecho Filtrado"))
            .andExpect(jsonPath("$[0].fuente.moduloFuente.identificador").value(200L))
            .andExpect(jsonPath("$[0].fuente.moduloFuente.url").value("http://modulo2"));
  }
}
