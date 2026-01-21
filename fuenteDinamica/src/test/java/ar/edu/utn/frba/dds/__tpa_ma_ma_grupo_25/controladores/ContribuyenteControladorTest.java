package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@WebMvcTest(ContribuyenteControlador.class)
public class ContribuyenteControladorTest {

  @Autowired
  private MockMvc mockMvc;

  private IContribuyenteServicio contribuyenteServicio;

  @Test
  void testCrearContribuyente() {
    IContribuyenteServicio mockServicio = mock(IContribuyenteServicio.class);
    ContribuyenteControlador controlador = new ContribuyenteControlador(mockServicio);

    ContribuyenteInputDTO input = new ContribuyenteInputDTO();
    input.setNombre("Juan");
    input.setApellido("Pérez");
    input.setEdad(30);

    ContribuyenteOutputDTO output = new ContribuyenteOutputDTO(
            1L, "Juan", "Pérez", 30, List.of()
    );

    doAnswer(inv -> output).when(mockServicio).crear("Juan", "Pérez", 30);

    Contribuyente resultado = controlador.crear(input);

    assertNotNull(resultado);
    assertEquals("Juan", resultado.getNombre());
  }

}
