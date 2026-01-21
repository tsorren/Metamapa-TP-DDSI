package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CriterioDePertenenciaTest {

  private Hecho hechoEjemplo;

  @BeforeEach
  void setUp() {
    hechoEjemplo = Hecho.crear(
            1L,
            "Título Prueba",
            "Descripción de prueba",
            new Categoria("Salud"),
            new Ubicacion(-34.6037, -58.3816), // CABA
            LocalDate.of(2023, 6, 1),
            LocalDateTime.now(),                // fechaDeCarga
            LocalDateTime.now(),                // fechaUltimaModificacion
            new Multimedia(1L, TipoContenido.AUDIO, "url"),
            TipoOrigen.EXTERNO,
            List.of(new Etiqueta("prueba")),
            true
    );
    hechoEjemplo.setFechaDeCarga(LocalDateTime.of(2023, 6, 5, 10, 0));
  }

  @Test
  void testCumpleConTodosLosFiltros() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.agregarFiltro(new FiltroTitulo("Título Prueba"));
    criterio.agregarFiltro(new FiltroCategoria(new Categoria("Salud")));
    criterio.agregarFiltro(new FiltroUbicacion(new Ubicacion(-34.6037, -58.3816), 5.0));
    criterio.agregarFiltro(new FiltroFechaExacta(LocalDate.of(2023, 6, 1)));
    criterio.agregarFiltro(new FiltroCargaEntre(
            LocalDateTime.of(2023, 6, 4, 0, 0),
            LocalDateTime.of(2023, 6, 6, 23, 59)
    ));

    assertTrue(criterio.cumpleCon(hechoEjemplo));
    assertEquals(1, criterio.aplicar(List.of(hechoEjemplo)).size());
  }

  @Test
  void testNoCumpleConUnFiltro() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.agregarFiltro(new FiltroCategoria(new Categoria("Transporte"))); // Falla acá

    assertFalse(criterio.cumpleCon(hechoEjemplo));
    assertTrue(criterio.aplicar(List.of(hechoEjemplo)).isEmpty());
  }

  @Test
  void testAgregarYQuitarFiltro() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    Filtro filtro = new FiltroTitulo("Título Prueba");

    criterio.agregarFiltro(filtro);
    assertEquals(1, criterio.getFiltros().size());

    criterio.quitarFiltro(filtro);
    assertEquals(0, criterio.getFiltros().size());
  }
}
