package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxy;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.TipoFuenteProxy;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

public class FuenteProxyStub extends FuenteProxy {

  public FuenteProxyStub() {
    super(WebClient.builder().baseUrl("http://localhost").build(), "http://localhost", TipoFuenteProxy.DESASTRESAPI);
  }

  @Override
  public List<Hecho> hechos() {
    return Collections.emptyList();
  }

  @Override
  public Hecho hechoPorId(Long idHecho) {
    return null;
  }
}

