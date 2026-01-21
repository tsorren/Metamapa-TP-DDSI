package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.metamapa.HechoMetamapaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteProxyServicio;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

public class FuenteMetamapa extends FuenteProxy {
  public FuenteMetamapa(WebClient api, String baseUrl) {
    super(api, baseUrl, TipoFuenteProxy.METAMAPA);
  }

  public List<Hecho> hechos() {
    List<HechoMetamapaInputDTO> response = this.getApi().get()
            .uri(uriBuilder -> uriBuilder
                    .path("/hechos")
                    .build())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HechoMetamapaInputDTO>>() {
            })
            .block();

    if (response == null) {
      return Collections.emptyList();
    }
    return response.stream().map(FuenteProxyServicio::hechoMetamapaInputDTO).toList();
  }

  public Hecho hechoPorId(Long idHecho) {
    HechoMetamapaInputDTO response = this.getApi().get()
            .uri(uriBuilder -> uriBuilder
                    .path("/hechos/{id}")
                    .build(idHecho))
            .retrieve()
            .bodyToMono(HechoMetamapaInputDTO.class)
            .block();

    if (response == null) {
      return null;
    }
    return FuenteProxyServicio.hechoMetamapaInputDTO(response);
  }
}
