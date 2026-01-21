package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SolicitudDeCreacionControlador.class)
public class SolicitudDeCreacionControladorTest {

  @Autowired
  private MockMvc mockMvc;


  private ISolicitudDeCreacionServicio solicitudDeCreacionServicio;

  @Test
  void testProcesarSolicitud() throws Exception {
    SolicitudDeCreacionOutputDTO dto = new SolicitudDeCreacionOutputDTO(1L, LocalDateTime.now(), List.of(), null, null);
    when(solicitudDeCreacionServicio.procesar(any(), any(), any(), any())).thenReturn(dto);

    String json = """
              {
                "idSolicitud": 1,
                "idResponsable": 1,
                "nuevoEstado": "APROBADO",
                "feedback": "Todo bien"
              }
            """;

    mockMvc.perform(
                    patch("/solicitudesDeCreacion/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.identificador").value(1));
  }


  @Test
  void testEncontrarTodasSolicitudes() {
    ISolicitudDeCreacionServicio mockServicio = mock(ISolicitudDeCreacionServicio.class);
    SolicitudDeCreacionControlador controlador = new SolicitudDeCreacionControlador(mockServicio);

    SolicitudDeCreacionOutputDTO dto = new SolicitudDeCreacionOutputDTO(
            1L,
            LocalDateTime.now(),
            List.of(),
            mock(HechoOutputDTO.class),
            mock(ContribuyenteOutputDTO.class)
    );

    doAnswer(inv -> List.of(dto)).when(mockServicio).encontrarTodas(null);

    List<SolicitudDeCreacionOutputDTO> resultado = controlador.encontrarTodas(null);

    assertNotNull(resultado);
    assertEquals(1, resultado.size());
    assertEquals(1L, resultado.get(0).getIdentificador());
  }

}
