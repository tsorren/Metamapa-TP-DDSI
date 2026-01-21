package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.FuenteControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FuenteControlador.class)
public class FuenteControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IFuenteServicio fuenteServicio;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testHechosDeFuente() throws Exception {
    // Crear datos mock de Hecho
    Categoria categoria = new Categoria("incendio");

    Ubicacion ubicacion = new Ubicacion(10.0, 20.0);

    Multimedia multimedia = new Multimedia(1L, TipoContenido.IMAGEN, "http://url.imagen");

    Hecho hecho1 = Hecho.builder()
            .identificador(1L)
            .identificadorExterno(100L)
            .titulo("Hecho 1")
            .descripcion("Descripcion 1")
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(LocalDate.of(2023, 5, 1))
            .fechaDeCarga(LocalDateTime.of(2023, 5, 2, 10, 0))
            .fechaUltimaModificacion(LocalDateTime.of(2023, 5, 3, 10, 0))
            .contenido(multimedia)
            .origen(TipoOrigen.MANUAL)
            .visible(true)
            .etiquetas(List.of(new Etiqueta("Etiqueta1")))
            .build();

    Hecho hecho2 = Hecho.builder()
            .identificador(2L)
            .identificadorExterno(101L)
            .titulo("Hecho 2")
            .descripcion("Descripcion 2")
            .categoria(categoria)
            .ubicacion(ubicacion)
            .fechaDelHecho(LocalDate.of(2023, 6, 1))
            .fechaDeCarga(LocalDateTime.of(2023, 6, 2, 10, 0))
            .fechaUltimaModificacion(LocalDateTime.of(2023, 6, 3, 10, 0))
            .contenido(multimedia)
            .origen(TipoOrigen.CONTRIBUYENTE)
            .visible(true)
            .etiquetas(List.of(new Etiqueta("Etiqueta2")))
            .build();

    // Mock del servicio
    Mockito.when(fuenteServicio.hechosDeFuente(anyLong())).thenReturn(List.of(hecho1, hecho2));

    // Ejecutar petición GET y verificar respuesta
    mockMvc.perform(get("/fuentes/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].identificador", is(1)))
            .andExpect(jsonPath("$[0].titulo", is("Hecho 1")))
            .andExpect(jsonPath("$[0].descripcion", is("Descripcion 1")))
            .andExpect(jsonPath("$[0].categoria.nombre", is("Incendio")))
            .andExpect(jsonPath("$[0].ubicacion.latitud", is(10.0)))
            .andExpect(jsonPath("$[0].contenido.tipoContenido", is("IMAGEN")))
            .andExpect(jsonPath("$[0].etiquetas[0].nombre", is("Etiqueta1")))
            .andExpect(jsonPath("$[1].identificador", is(2)))
            .andExpect(jsonPath("$[1].origen", is("CONTRIBUYENTE")))
            .andExpect(jsonPath("$[1].etiquetas[0].nombre", is("Etiqueta2")));
  }
}
