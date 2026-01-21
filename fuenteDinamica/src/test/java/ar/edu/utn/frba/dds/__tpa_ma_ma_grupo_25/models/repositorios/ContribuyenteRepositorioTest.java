package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ContribuyenteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ContribuyenteRepositorioTest {

  private ContribuyenteRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new ContribuyenteRepositorio();
  }

  @Test
  @DisplayName("Guarda un contribuyente con ID")
  void guardaContribuyente() {
    Contribuyente c = Contribuyente.crear("Ana", "Pérez");
    repositorio.guardar(c);
    assertNotNull(c.getIdentificador());
  }

  @Test
  @DisplayName("Encuentra un contribuyente por ID")
  void encuentraPorId() {
    Contribuyente c = Contribuyente.crear("Juan", "Lopez");
    repositorio.guardar(c);
    assertEquals(c, repositorio.encontrarPorId(c.getIdentificador()));
  }

  @Test
  @DisplayName("Verifica existencia de contribuyente")
  void existeContribuyente() {
    Contribuyente c = Contribuyente.crear("Lucía");
    repositorio.guardar(c);
    assertTrue(repositorio.existe(c.getIdentificador()));
  }

  @Test
  @DisplayName("Encuentra todos los contribuyentes")
  void encuentraTodos() {
    repositorio.guardar(Contribuyente.crear("X"));
    repositorio.guardar(Contribuyente.crear("Y"));
    assertEquals(2, repositorio.encontrarTodas().size());
  }
}

