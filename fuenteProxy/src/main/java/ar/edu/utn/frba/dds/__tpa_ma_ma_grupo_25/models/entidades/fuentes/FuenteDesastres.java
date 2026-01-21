package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.DataResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.HechoExternoInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.LoginDataResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi.LoginResponseInput;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.IntStream;

@Getter
public class FuenteDesastres extends FuenteProxy {
  private final String token;
  private final Integer perPage;
  private final String email;
  private final String password;

  public FuenteDesastres(WebClient api, String baseUrl, String email, String password) {
    super(api, baseUrl, TipoFuenteProxy.DESASTRESAPI);
    this.email = email;
    this.password = password;
    this.token = login(email, password);
    perPage = 100; // Cantidad de hechos por request de pagina (1-100)
  }

  private static Hecho hechoExternoInput(HechoExternoInput h) {
    return Hecho.builder()
            .identificador(h.getId())
            .titulo(h.getTitulo())
            .descripcion(h.getDescripcion())
            .categoria(new Categoria(h.getCategoria()))
            .ubicacion(new Ubicacion(h.getLatitud(), h.getLongitud()))
            .fechaDelHecho(h.getFecha_hecho().toLocalDate())
            .fechaDeCarga(h.getCreated_at().toLocalDateTime())
            .origen(TipoOrigen.EXTERNO)
            .contenido(null)
            .etiquetas(new HashSet<>())
            .visible(true)
            .build();
  }

  private static List<Hecho> dataResponseInput(DataResponseInput response) {
    return new ArrayList<>(response.getData().stream()
            .map(FuenteDesastres::hechoExternoInput)
            .toList());
  }

  private String login(String email, String password) {
    Map<String, String> credencial = new HashMap<>();
    credencial.put("email", email);
    credencial.put("password", password);

    LoginDataResponseInput response = this.getApi().post()
            .uri("/login")
            .bodyValue(credencial)
            .retrieve()
            .bodyToMono(LoginResponseInput.class)
            .map(LoginResponseInput::getData)
            .block();

    if (response != null) {
      //System.out.println("Token recibido y almacenado: " + this.token);
      return response.getAccess_token();
    }
    return null;
  }

  private DataResponseInput pedirPagina(Integer numeroDePagina) {
    try {
      DataResponseInput response = this.getApi().get()
              .uri(uriBuilder -> uriBuilder
                      .path("/desastres")
                      .queryParam("page", numeroDePagina)
                      .queryParam("per_page", perPage)
                      .build())
              .header("Authorization", "Bearer " + this.token)
              .retrieve()
              .bodyToMono(DataResponseInput.class)
              .block();

      if (response == null || response.getData() == null) {
        return null;
      }
      return response;
    } catch (Exception e) {
      // Log error and return null to allow graceful degradation
      return null;
    }
  }

  @Override
  public List<Hecho> hechos() {
    // Primero hacemos una llamada para obtener el total
    DataResponseInput pagina = this.pedirPagina(1);

    if (pagina == null) {
      // External API is unavailable, return empty list
      return Collections.emptyList();
    }
    int totalDePaginas = pagina.getLast_page(); // Necesitas este dato del JSON

    List<Hecho> hechos = Collections.synchronizedList(new ArrayList<>());
    hechos.addAll(dataResponseInput(pagina));

    // Usamos parallelStream para lanzar peticiones en paralelo
    List<Hecho> hechosRestantes = IntStream.rangeClosed(2, totalDePaginas).parallel()
            .mapToObj(this::pedirPagina).filter(Objects::nonNull)
            .map(FuenteDesastres::dataResponseInput)
            .flatMap(List::stream)
            .toList();

    hechos.addAll(hechosRestantes);
    return hechos;
  }

  @Override
  public Hecho hechoPorId(Long idHecho) {
    try {
      HechoExternoInput hechoExterno = this.getApi().get()
              .uri("/desastres/{id}", idHecho)
              .header("Authorization", "Bearer " + this.token)
              .retrieve()
              .bodyToMono(HechoExternoInput.class)
              .block();

      if (hechoExterno == null) {
        return null;
      }
      return hechoExternoInput(hechoExterno);
    } catch (Exception e) {
      return null;
    }
  }
}
