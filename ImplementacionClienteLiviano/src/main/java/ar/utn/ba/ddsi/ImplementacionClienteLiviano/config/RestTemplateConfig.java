package ar.utn.ba.ddsi.ImplementacionClienteLiviano.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    // Configurar timeouts - aumentado para importaciones largas de CSV
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(30000);  // 30 segundos para conexión
    factory.setReadTimeout(300000);     // 5 minutos para lectura (importaciones largas)
    restTemplate.setRequestFactory(factory);

    return restTemplate;
  }
}
