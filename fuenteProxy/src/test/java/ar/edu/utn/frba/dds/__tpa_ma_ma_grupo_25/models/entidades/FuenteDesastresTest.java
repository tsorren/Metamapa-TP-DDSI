package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.DataResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.HechoExternoInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.LoginDataResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.LoginResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteDesastres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FuenteDesastresTest {

  private WebClient webClientMock;
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  void setup() {
    webClientMock = mock(WebClient.class);
    requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    responseSpec = mock(WebClient.ResponseSpec.class);
  }

  @Test
  void testHechos() {
    // Preparar login
    LoginDataResponseInput loginData = new LoginDataResponseInput();
    loginData.setAccess_token("fake-token");
    LoginResponseInput loginResponse = new LoginResponseInput();
    loginResponse.setData(loginData);

    // Preparar página 1
    HechoExternoInput hechoInput = new HechoExternoInput();
    hechoInput.setId(1L);
    hechoInput.setTitulo("Título");
    hechoInput.setDescripcion("Descripción");
    hechoInput.setCategoria("Incendio");
    hechoInput.setLatitud(10.0);
    hechoInput.setLongitud(20.0);
    hechoInput.setFecha_hecho(OffsetDateTime.now());
    hechoInput.setCreated_at(OffsetDateTime.now());
    hechoInput.setUpdated_at(OffsetDateTime.now());

    DataResponseInput pagina1 = new DataResponseInput();
    pagina1.setCurrent_page(1);
    pagina1.setLast_page(1);
    pagina1.setData(List.of(hechoInput));

    // Mock Login
    when(webClientMock.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/login")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(LoginResponseInput.class)).thenReturn(Mono.just(loginResponse));

    // Mock pedirPagina(1)
    when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.header(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DataResponseInput.class)).thenReturn(Mono.just(pagina1));

    // Crear instancia
    FuenteDesastres fuente = new FuenteDesastres(webClientMock, "http://mock.api", "mail", "pass");

    List<Hecho> hechos = fuente.hechos();

    assertNotNull(hechos);
    assertEquals(1, hechos.size());
    assertEquals("Título", hechos.get(0).getTitulo());
    assertEquals("Descripción", hechos.get(0).getDescripcion());
    assertEquals("Incendio", hechos.get(0).getCategoria().getNombre());
  }

  @Test
  void testHechoPorId() {
    HechoExternoInput hechoExterno = new HechoExternoInput();
    hechoExterno.setId(2L);
    hechoExterno.setTitulo("Hecho único");
    hechoExterno.setDescripcion("Detalle");
    hechoExterno.setCategoria("Inundación");
    hechoExterno.setLatitud(5.5);
    hechoExterno.setLongitud(9.9);
    hechoExterno.setFecha_hecho(OffsetDateTime.now());
    hechoExterno.setCreated_at(OffsetDateTime.now());
    hechoExterno.setUpdated_at(OffsetDateTime.now());

    // Login
    LoginDataResponseInput loginData = new LoginDataResponseInput();
    loginData.setAccess_token("fake-token");
    LoginResponseInput loginResponse = new LoginResponseInput();
    loginResponse.setData(loginData);

    // Login mock
    when(webClientMock.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri("/login")).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(LoginResponseInput.class)).thenReturn(Mono.just(loginResponse));

    // GET hecho por ID mock
    when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri("/desastres/{id}", 2L)).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.header(anyString(), anyString())).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HechoExternoInput.class)).thenReturn(Mono.just(hechoExterno));

    FuenteDesastres fuente = new FuenteDesastres(webClientMock, "http://mock.api", "mail", "pass");

    Hecho hecho = fuente.hechoPorId(2L);

    assertNotNull(hecho);
    assertEquals("Hecho único", hecho.getTitulo());
    assertEquals("Inundación", hecho.getCategoria().getNombre());
  }
}
