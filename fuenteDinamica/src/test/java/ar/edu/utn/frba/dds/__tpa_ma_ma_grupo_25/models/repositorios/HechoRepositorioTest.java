package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.HechoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public @DisplayName("Tests de HechoRepositorio")
class HechoRepositorioTest {
  private HechoRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new HechoRepositorio();
  }

  @Test
  @DisplayName("Guarda un hecho con ID")
  void guardaHecho() {
    Hecho hecho = Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL);
    repositorio.guardar(hecho);
    assertNotNull(hecho.getIdentificador());
  }

  @Test
  @DisplayName("Encuentra hecho por ID")
  void encuentraPorId() {
    Hecho hecho = Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL);
    repositorio.guardar(hecho);
    assertEquals(hecho, repositorio.encontrarPorId(hecho.getIdentificador()));
  }

  @Test
  @DisplayName("Encuentra todos los hechos")
  void encuentraTodos() {
    repositorio.guardar(Hecho.crear("incendio", "fuego", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL));
    repositorio.guardar(Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL));
    assertEquals(2, repositorio.encontrarTodos().size());
  }
}

