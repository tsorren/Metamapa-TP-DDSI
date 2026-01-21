package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModuloFuenteTest {

  @Test
  void crearModuloFuente_conParametrosCorrectos_valoresAsignadosCorrectamente() {
    WebClient apiMock = WebClient.builder().baseUrl("http://test.com").build();

    ModuloFuente modulo = new ModuloFuente(TipoFuente.ESTATICA, "http://test.com", apiMock);

    assertEquals(TipoFuente.ESTATICA, modulo.getTipo());
    assertEquals("http://test.com", modulo.getBaseUrl());
    assertEquals(apiMock, modulo.getApi());
    assertNull(modulo.getIdentificador());
  }

  @Test
  void setIdentificador_funcionaCorrectamente() {
    WebClient apiMock = WebClient.builder().baseUrl("http://api.url").build();
    ModuloFuente modulo = new ModuloFuente(TipoFuente.DINAMICA, "http://api.url", apiMock);

    modulo.setIdentificador(100L);

    assertEquals(100L, modulo.getIdentificador());
  }
}
