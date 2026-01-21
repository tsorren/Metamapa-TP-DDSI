package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteProxyOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxy;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteProxyRepositorio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteProxyServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FuenteProxyServicioTest {

  private IFuenteProxyRepositorio mockRepo;
  private WebClient.Builder mockBuilder;
  private FuenteProxyServicio servicio;

  @BeforeEach
  void setUp() {
    mockRepo = mock(IFuenteProxyRepositorio.class);
    mockBuilder = mock(WebClient.Builder.class);
    WebClient webClientMock = mock(WebClient.class);
    when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
    when(mockBuilder.build()).thenReturn(webClientMock);

    servicio = new FuenteProxyServicio(
            mockRepo,
            mockBuilder,
            "ddsi@gmail.com",
            "ddsi2025*",
            "https://api-ddsi.disilab.ar/public/api"
    );
  }

  @Test
  void testCrearFuenteProxy() {
    FuenteProxyOutputDTO result = servicio.crear("https://metamapa.ar/api");
    assertNotNull(result);
    verify(mockRepo, times(2)).guardar(any(FuenteProxy.class)); // 1 por constructor, 1 por crear
  }

  @Test
  void testEncontrarFuentes() {
    FuenteProxy fuente = mock(FuenteProxy.class);
    when(fuente.getIdentificador()).thenReturn(123L);
    when(mockRepo.encontrarTodas()).thenReturn(List.of(fuente));

    List<FuenteProxyOutputDTO> result = servicio.encontrarFuentes();

    assertEquals(1, result.size());
    assertEquals(123L, result.get(0).getIdentificador());
  }

  @Test
  void testEncontrarHechos() {
    Hecho hecho = buildHecho(1L);
    FuenteProxy fuente = mock(FuenteProxy.class);
    when(fuente.hechos()).thenReturn(List.of(hecho));
    when(mockRepo.encontrarPorId(1L)).thenReturn(fuente);

    List<HechoOutputDTO> hechos = servicio.encontrarHechos(1L);

    assertEquals(1, hechos.size());
    assertEquals("Titulo", hechos.get(0).getTitulo());
  }

  @Test
  void testEncontrarHechoPorId() {
    Hecho hecho = buildHecho(42L);
    FuenteProxy fuente = mock(FuenteProxy.class);
    when(fuente.hechoPorId(42L)).thenReturn(hecho);
    when(mockRepo.encontrarPorId(1L)).thenReturn(fuente);

    HechoOutputDTO dto = servicio.encontrarHechoPorId(1L, 42L);

    assertNotNull(dto);
    assertEquals(42L, dto.getIdentificador());
  }

  // Helper
  private Hecho buildHecho(Long id) {
    return new Hecho(
            id,
            "Titulo",
            "Descripcion",
            null,
            null,
            LocalDate.now(),
            LocalDateTime.now(),
            null,
            null,
            Set.of(),
            true
    );
  }
}
