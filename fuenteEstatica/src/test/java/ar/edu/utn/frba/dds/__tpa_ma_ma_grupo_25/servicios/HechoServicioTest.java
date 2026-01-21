package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.HechoServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HechoServicioTest {

  @Mock
  private IHechoRepositorio hechoRepositorio;

  @InjectMocks
  private HechoServicio servicio;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void encontrarTodosRetornaDTOs() {
    Hecho hecho = Hecho.builder()
            .identificador(1L)
            .titulo("Un hecho")
            .descripcion("desc")
            .visible(true)
            .build();

    Mockito.when(hechoRepositorio.encontrarTodos()).thenReturn(List.of(hecho));

    List<HechoOutputDTO> hechos = servicio.encontrarTodos();

    Assertions.assertEquals(1, hechos.size());
    Assertions.assertEquals("Un hecho", hechos.get(0).getTitulo());
  }

  @Test
  public void ocultarHechoCambiaVisibilidad() {
    Hecho hecho = Hecho.builder()
            .identificador(1L)
            .visible(true)
            .build();

    Mockito.when(hechoRepositorio.encontrarPorId(1L)).thenReturn(hecho);

    servicio.ocultarHecho(1L);

    Assertions.assertFalse(hecho.getVisible());
  }
}
