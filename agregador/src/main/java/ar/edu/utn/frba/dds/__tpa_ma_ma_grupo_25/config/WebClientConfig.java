package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient.Builder webClientBuilder() {
    // Configurar un pool de conexiones con timeouts extendidos
    ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
            .maxConnections(50)
            .maxIdleTime(Duration.ofMinutes(10))
            .maxLifeTime(Duration.ofMinutes(30))
            .pendingAcquireTimeout(Duration.ofMinutes(2))
            .evictInBackground(Duration.ofMinutes(5))
            .build();

    HttpClient httpClient = HttpClient.create(connectionProvider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 60s para conectar
            .option(ChannelOption.SO_KEEPALIVE, true)
            .responseTimeout(Duration.ofMinutes(15)) // 15 minutos para respuesta completa
            .doOnConnected(conn -> conn
                    .addHandlerFirst(new ReadTimeoutHandler(900, TimeUnit.SECONDS)) // 15 minutos para leer
                    .addHandlerLast(new WriteTimeoutHandler(300, TimeUnit.SECONDS))); // 5 minutos para escribir

    return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(512 * 1024 * 1024)); // 512MB
  }
}