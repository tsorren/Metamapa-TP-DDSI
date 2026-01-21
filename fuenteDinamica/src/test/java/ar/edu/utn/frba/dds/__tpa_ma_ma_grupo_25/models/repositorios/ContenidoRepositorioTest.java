package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoContenido;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.ContenidoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContenidoRepositorioTest {

  private ContenidoRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new ContenidoRepositorio();
  }

  @Test
  @DisplayName("Guarda contenido multimedia con ID autogenerado")
  void guardarContenidoGeneraId() {
    Multimedia contenido = new Multimedia(1L, TipoContenido.IMAGEN, "https://cdn.pixabay.com/photo/2023/01/15/14/31/viking-7718981_960_720.jpg\n");
    repositorio.guardar(contenido);

    assertNotNull(contenido.getIdentificador());
    assertEquals(contenido, repositorio.encontrarPorId(contenido.getIdentificador()));
  }

  @Test
  @DisplayName("Encuentra todos los contenidos guardados")
  void encontrarTodosLosContenidos() {
    repositorio.guardar(new Multimedia(1L, TipoContenido.IMAGEN, "https://cdn.pixabay.com/photo/2023/01/15/14/31/viking-7718981_960_720.jpg\n"));
    repositorio.guardar(new Multimedia(2L, TipoContenido.VIDEO, "https://cdn.pixabay.com/photo/2023/01/15/14/31/viking-7718981_960_720.jpg\n"));

    List<Multimedia> todos = repositorio.encontrarTodos();
    assertEquals(2, todos.size());
  }

  @Test
  @DisplayName("Retorna null si el contenido no existe")
  void contenidoNoExiste() {
    assertNull(repositorio.encontrarPorId(77L));
  }
}
