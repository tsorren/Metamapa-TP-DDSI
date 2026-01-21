package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.CriterioDePertenenciaControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.FiltroCategoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICriterioDePertenenciaServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CriterioDePertenenciaControlador.class)
public class CriterioDePertenenciaControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private ICriterioDePertenenciaServicio criterioServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testEncontrarTodos() throws Exception {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.setIdentificador(1L);

    when(criterioServicio.encontrarTodos()).thenReturn(List.of(criterio));

    mockMvc.perform(get("/criteriosDePertenencia/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].identificador").value(1));
  }

  @Test
  void testCrearCriterio() throws Exception {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.setIdentificador(1L);

    when(criterioServicio.crear()).thenReturn(criterio);

    mockMvc.perform(post("/criteriosDePertenencia/crear"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1));
  }

  @Test
  void testAgregarFiltro() throws Exception {
    Categoria categoria = new Categoria("Emergencia");
    FiltroCategoria filtro = new FiltroCategoria(categoria);
    filtro.setIdentificador(10L);

    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.setIdentificador(1L);
    criterio.agregarFiltro(filtro);

    when(criterioServicio.agregarFiltro(1L, 10L)).thenReturn(criterio);

    mockMvc.perform(patch("/criteriosDePertenencia/agregarFiltro")
                    .param("idCriterio", "1")
                    .param("idFiltro", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1))
            .andExpect(jsonPath("$.filtros[0].tipo").value("Categoria"))
            .andExpect(jsonPath("$.filtros[0].identificador").value(10));
  }

  @Test
  void testQuitarFiltro() throws Exception {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.setIdentificador(1L); // Ya sin filtros

    when(criterioServicio.quitarFiltro(1L, 10L)).thenReturn(criterio);

    mockMvc.perform(patch("/criteriosDePertenencia/quitarFiltro")
                    .param("idCriterio", "1")
                    .param("idFiltro", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1))
            .andExpect(jsonPath("$.filtros").isEmpty());
  }
}
