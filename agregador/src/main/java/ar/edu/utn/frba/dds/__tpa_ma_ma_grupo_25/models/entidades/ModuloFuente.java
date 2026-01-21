package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ModuloFuenteOutputDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Entity
@Table(name = "modulos_fuente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuloFuente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_fuente", nullable = false)
  private TipoFuente tipo;

  @Column(name = "base_url", nullable = false)
  private String baseUrl;

  @Transient
  @JsonIgnore
  private WebClient api;

  public ModuloFuente(TipoFuente tipo, String baseUrl, WebClient api) {
    this.tipo = tipo;
    this.baseUrl = baseUrl;
    this.api = api;
  }

  @PostLoad
  public void initApi() {
    // Asegurar que la URL tenga protocolo
    String urlConProtocolo = this.baseUrl;
    if (!urlConProtocolo.startsWith("http://") && !urlConProtocolo.startsWith("https://")) {
      // En Railway, usar HTTPS por defecto
      urlConProtocolo = "https://" + urlConProtocolo;
      log.warn("URL sin protocolo detectada. Usando HTTPS: {}", urlConProtocolo);
    }

    // Configurar HttpClient con timeouts extendidos para operaciones largas (importación CSV)
    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 60s para conectar
            .option(ChannelOption.SO_KEEPALIVE, true) // Mantener conexión viva
            .responseTimeout(java.time.Duration.ofMinutes(15)) // 15 minutos para respuesta completa
            .doOnConnected(conn -> conn
                    .addHandlerFirst(new ReadTimeoutHandler(900, TimeUnit.SECONDS)) // 15 minutos para leer
                    .addHandlerLast(new WriteTimeoutHandler(300, TimeUnit.SECONDS))) // 5 minutos para escribir
            .followRedirect(true); // Seguir redirecciones HTTP 301/302

    // Crear WebClient con límite de buffer aumentado (512MB) para manejar respuestas grandes
    this.api = WebClient.builder()
            .baseUrl(urlConProtocolo)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(512 * 1024 * 1024)) // 512MB
            .build();
  }

  public ModuloFuenteOutputDTO toDTO() {
    return new ModuloFuenteOutputDTO(this.id, this.baseUrl, this.tipo);
  }
}
