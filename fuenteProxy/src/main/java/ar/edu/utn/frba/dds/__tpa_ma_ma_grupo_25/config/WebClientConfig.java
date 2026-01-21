package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    HttpClient httpClient = HttpClient.create()
            .followRedirect(true) // Enable automatic redirect following
            .responseTimeout(java.time.Duration.ofSeconds(60));

    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(512 * 1024 * 1024)); // 512MB
  }
}