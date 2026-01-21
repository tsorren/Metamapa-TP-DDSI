package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FuenteTest {

  @Test
  void crearFuente_conParametrosCorrectos_valoresAsignadosCorrectamente() {
    WebClient apiMock = WebClient.builder().baseUrl("http://base.url").build();
    ModuloFuente modulo = new ModuloFuente(TipoFuente.PROXY, "http://base.url", apiMock);
    LocalDateTime fechaCreacion = LocalDateTime.now();

    Fuente fuente = new Fuente(modulo, 42L, fechaCreacion);

    assertEquals(modulo, fuente.getModulo());
    assertEquals(42L, fuente.getIdentificadorExterno());
    assertEquals(fechaCreacion, fuente.getFechaDeCreacion());
    assertNull(fuente.getIdentificador());
  }

  @Test
  void setIdentificador_funcionaCorrectamente() {
    ModuloFuente modulo = new ModuloFuente(TipoFuente.ESTATICA, "http://static.url", WebClient.create());
    Fuente fuente = new Fuente(modulo, 7L, LocalDateTime.now());

    fuente.setIdentificador(55L);

    assertEquals(55L, fuente.getIdentificador());
  }
}
