package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Getter
public abstract class FuenteProxy {
  protected final WebClient api;
  protected final String baseUrl;
  protected final TipoFuenteProxy tipo;
  @Setter
  protected Long identificador;

  public FuenteProxy(WebClient api, String baseUrl, TipoFuenteProxy tipo) {
    this.api = api;
    this.baseUrl = baseUrl;
    this.tipo = tipo;
  }

  public abstract List<Hecho> hechos();

  public abstract Hecho hechoPorId(Long idHecho);

}
