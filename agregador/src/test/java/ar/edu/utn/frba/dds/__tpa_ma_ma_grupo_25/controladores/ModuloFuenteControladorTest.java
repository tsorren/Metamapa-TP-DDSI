package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.ModuloFuenteControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ModuloFuenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ModuloFuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@WebMvcTest(ModuloFuenteControlador.class)
public class ModuloFuenteControladorTest {

  private IModuloFuenteServicio moduloFuenteServicio;
  private ModuloFuenteControlador moduloFuenteControlador;

  @BeforeEach
  public void setup() {
    moduloFuenteServicio = mock(IModuloFuenteServicio.class);
    moduloFuenteControlador = new ModuloFuenteControlador(moduloFuenteServicio);
  }

  @Test
  public void testCrearModuloFuente() {
    // Preparar input DTO con valores no nulos
    ModuloFuenteInputDTO inputDTO = new ModuloFuenteInputDTO();
    inputDTO.setTipo(TipoFuente.ESTATICA);
    inputDTO.setUrl("http://example.com/api");

    // Mock respuesta esperada
    ModuloFuenteOutputDTO outputDTO = new ModuloFuenteOutputDTO(1L, "http://example.com/api", TipoFuente.ESTATICA);
    when(moduloFuenteServicio.crear(inputDTO.getTipo(), inputDTO.getUrl())).thenReturn(outputDTO);

    // Ejecutar método
    ModuloFuenteOutputDTO resultado = moduloFuenteControlador.crear(inputDTO);

    // Verificar interacción y resultados
    verify(moduloFuenteServicio).crear(inputDTO.getTipo(), inputDTO.getUrl());
    assertNotNull(resultado);
    assertEquals(1L, resultado.getIdentificador());
    assertEquals("http://example.com/api", resultado.getUrl());
  }

  @Test
  public void testConsultarFuentes() {
    // Preparar mock respuesta
    List<ModuloFuenteOutputDTO> listaMock = List.of(
            new ModuloFuenteOutputDTO(1L, "http://example.com/api", TipoFuente.ESTATICA),
            new ModuloFuenteOutputDTO(2L, "http://otro.com/api", TipoFuente.DINAMICA)
    );

    when(moduloFuenteServicio.encontrarTodas()).thenReturn(listaMock);

    // Ejecutar método
    List<ModuloFuenteOutputDTO> resultado = moduloFuenteControlador.consultarFuentes();

    // Verificar interacción y resultados
    verify(moduloFuenteServicio).encontrarTodas();
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals("http://example.com/api", resultado.get(0).getUrl());
  }
}
