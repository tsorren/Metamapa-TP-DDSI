package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CategoriaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISincronizadorCategoriaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SincronizadorCategoriaServicio implements ISincronizadorCategoriaServicio {

  private final RestTemplate restTemplate;
  private final List<String> urlsMicroservicios;

  public SincronizadorCategoriaServicio(
          RestTemplate restTemplate,
          @Value("${sync.categoria.agregador.url:http://localhost:8081/api/categorias/sincronizar}") String urlAgregador,
          @Value("${sync.categoria.fuenteEstatica.url:http://localhost:8083/api/categorias/sincronizar}") String urlFuenteEstatica,
          @Value("${sync.categoria.estadisticas.url:}") String urlEstadisticas
  ) {
    this.restTemplate = restTemplate;
    this.urlsMicroservicios = new ArrayList<>();

    // Agregar URLs que no estén vacías
    if (urlAgregador != null && !urlAgregador.isEmpty()) {
      urlsMicroservicios.add(urlAgregador);
    }
    if (urlFuenteEstatica != null && !urlFuenteEstatica.isEmpty()) {
      urlsMicroservicios.add(urlFuenteEstatica);
    }
    if (urlEstadisticas != null && !urlEstadisticas.isEmpty()) {
      urlsMicroservicios.add(urlEstadisticas);
    }
  }

  @Override
  public void sincronizarCategoriaEnTodosLosMicroservicios(Categoria categoria) {
    if (categoria == null || categoria.getNombre() == null) {
      log.warn("<SINCRONIZAR> Intento de sincronizar categoría nula o sin nombre");
      return;
    }

    CategoriaInputDTO dto = new CategoriaInputDTO(categoria.getNombre());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CategoriaInputDTO> request = new HttpEntity<>(dto, headers);

    for (String url : urlsMicroservicios) {
      try {
        log.info("<SINCRONIZAR> Enviando categoría '{}' a {}", categoria.getNombre(), url);
        restTemplate.postForObject(url, request, Object.class);
        log.info("<SINCRONIZAR> Categoría '{}' sincronizada exitosamente en {}", categoria.getNombre(), url);
      } catch (Exception e) {
        log.error("<SINCRONIZAR> Error al sincronizar categoría '{}' en {}: {}",
                  categoria.getNombre(), url, e.getMessage());
        // Continuar con los demás microservicios aunque uno falle
      }
    }
  }
}

