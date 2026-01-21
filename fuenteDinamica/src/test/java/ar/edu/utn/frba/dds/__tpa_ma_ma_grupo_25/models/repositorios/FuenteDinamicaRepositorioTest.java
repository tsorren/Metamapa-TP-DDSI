package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteDinamica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.FuenteDinamicaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public @DisplayName("Tests de FuenteDinamicaRepositorio")
class FuenteDinamicaRepositorioTest {
  private FuenteDinamicaRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new FuenteDinamicaRepositorio();
  }

  @Test
  @DisplayName("Guarda fuente dinámica y asigna ID")
  void guardaFuente() {
    FuenteDinamica fuente = new FuenteDinamica();
    repositorio.guardar(fuente);
    assertNotNull(fuente.getIdentificador());
  }

  @Test
  @DisplayName("Asocia hecho a fuente dinámica")
  void cargarHecho() {
    FuenteDinamica fuente = new FuenteDinamica();
    repositorio.guardar(fuente);
    repositorio.cargarHecho(fuente.getIdentificador(), 100L);
    assertEquals(List.of(100L), repositorio.idsDeHechosAsociados(fuente.getIdentificador()));
  }

  @Test
  @DisplayName("Encuentra todas las fuentes dinámicas")
  void encuentraTodas() {
    repositorio.guardar(new FuenteDinamica());
    repositorio.guardar(new FuenteDinamica());
    assertEquals(2, repositorio.encontrarTodas().size());
  }
}
