package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteEstaticaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteEstaticaServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
public class FuenteEstaticaServicioTest {

  @Mock
  private IFuenteEstaticaRepositorioBD fuenteRepositorio;
  @Mock
  private IHechoRepositorioBD hechoRepositorio;
  @Mock
  private AbstractErrorController errorController; // requerido por el constructor

  @InjectMocks
  private FuenteEstaticaServicio servicio;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    servicio = new FuenteEstaticaServicio(fuenteRepositorio, hechoRepositorio, errorController, "datos");
  }

  @Test
  public void encontrarTodasRetornaDTOs() {
    FuenteEstatica fuente = new FuenteEstatica(Paths.get("ficticia.csv"));
    fuente.setIdentificador(1L);

    Mockito.when(fuenteRepositorio.encontrarTodas()).thenReturn(List.of(fuente));

    List<FuenteOutputDTO> resultados = servicio.encontrarTodas();

    Assertions.assertEquals(1, resultados.size());
    Assertions.assertEquals(1L, resultados.get(0).getIdentificador());
  }
}
