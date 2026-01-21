package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.CategoriaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaRepositorioTest {

  private CategoriaRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new CategoriaRepositorio();
  }

  @Test
  @DisplayName("Guarda una categoría con identificador autogenerado")
  void guardarCategoriaConIdGenerado() {
    Categoria categoria = new Categoria("algo");
    repositorio.guardar(categoria);

    assertNotNull(categoria.getIdentificador());
    assertEquals(categoria, repositorio.encontrarPorId(categoria.getIdentificador()));
  }

  @Test
  @DisplayName("Encuentra todas las categorías guardadas")
  void encontrarTodasLasCategorias() {
    repositorio.guardar(new Categoria("algo"));
    repositorio.guardar(new Categoria("algo"));

    List<Categoria> categorias = repositorio.encontrarTodos();
    assertEquals(2, categorias.size());
  }

  @Test
  @DisplayName("Retorna null si no existe la categoría")
  void categoriaNoExiste() {
    assertNull(repositorio.encontrarPorId(123L));
  }
}
