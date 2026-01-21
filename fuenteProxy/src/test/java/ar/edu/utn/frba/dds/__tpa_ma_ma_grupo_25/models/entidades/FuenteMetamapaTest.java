package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.metamapa.HechoMetamapaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteMetamapa;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteProxyServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class FuenteMetamapaTest {

  private WebClient mockClient;
  private FuenteMetamapa fuente;

  @BeforeEach
  public void setUp() {
    mockClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
    fuente = new FuenteMetamapa(mockClient, "http://baseurl");
  }

  @Test
  public void testHechosDevuelveListaCorrecta() {
    HechoMetamapaInputDTO mockDto = mock(HechoMetamapaInputDTO.class);
    Hecho mockHecho = Hecho.builder().titulo("Corte de luz").descripcion("Zona oeste").build();
    List<HechoMetamapaInputDTO> lista = List.of(mockDto);

    when(mockClient.get().uri(any(Function.class)).retrieve()
            .bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(lista));

    // Mock conversión
    Hecho hechoMock = mock(Hecho.class);
    mockStatic(FuenteProxyServicio.class).when(() -> FuenteProxyServicio.hechoMetamapaInputDTO(mockDto))
            .thenReturn(hechoMock);

    List<Hecho> hechos = fuente.hechos();
    assertEquals(1, hechos.size());
  }

  @Test
  public void testHechosCuandoResponseEsNullDevuelveVacio() {
    when(mockClient.get().uri(any(Function.class)).retrieve()
            .bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.justOrEmpty(null));

    List<Hecho> hechos = fuente.hechos();
    assertTrue(hechos.isEmpty());
  }

  @Test
  public void testHechoPorIdDevuelveCorrecto() {
    HechoMetamapaInputDTO mockDto = mock(HechoMetamapaInputDTO.class);
    Hecho mockHecho = Hecho.builder().titulo("Corte de luz").descripcion("Zona oeste").build();

    when(mockClient.get().uri(any(Function.class)).retrieve()
            .bodyToMono(HechoMetamapaInputDTO.class)).thenReturn(Mono.just(mockDto));

    Hecho hechoMock = mock(Hecho.class);
    mockStatic(FuenteProxyServicio.class).when(() -> FuenteProxyServicio.hechoMetamapaInputDTO(mockDto))
            .thenReturn(hechoMock);

    Hecho hecho = fuente.hechoPorId(42L);
    assertNotNull(hecho);
  }

  @Test
  public void testHechoPorIdInexistenteDevuelveNull() {
    when(mockClient.get().uri(any(Function.class)).retrieve()
            .bodyToMono(HechoMetamapaInputDTO.class)).thenReturn(Mono.empty());

    Hecho hecho = fuente.hechoPorId(999L);
    assertNull(hecho);
  }
}
