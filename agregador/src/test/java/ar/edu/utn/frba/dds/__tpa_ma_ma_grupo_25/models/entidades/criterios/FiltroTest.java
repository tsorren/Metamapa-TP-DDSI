package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FiltroTest {

  private Hecho hecho;

  @BeforeEach
  void setUp() {
    hecho = Hecho.builder()
            .titulo("Inundación")
            .descripcion("Calle anegada")
            .categoria(new Categoria("Emergencia"))
            .ubicacion(new Ubicacion(-34.6, -58.4))
            .fechaDelHecho(LocalDate.of(2024, 4, 15))
            .fechaDeCarga(LocalDateTime.of(2024, 4, 16, 10, 30))
            .contenido(new Multimedia(1L, TipoContenido.IMAGEN, "https://ejemplo.com/img.jpg"))
            .origen(TipoOrigen.CONTRIBUYENTE)
            .etiquetas(List.of(new Etiqueta("inundacion")))
            .visible(true)
            .build();
  }

  @Test
  void filtroCategoriaCumple() {
    FiltroCategoria filtro = new FiltroCategoria(new Categoria("Emergencia"));
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroCategoriaNoCumple() {
    FiltroCategoria filtro = new FiltroCategoria(new Categoria("Seguridad"));
    assertThat(filtro.cumpleCon(hecho)).isFalse();
  }

  @Test
  void filtroDescripcionCumple() {
    FiltroDescripcion filtro = new FiltroDescripcion("Calle anegada");
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroFechaEntreCumple() {
    FiltroFechaEntre filtro = new FiltroFechaEntre(
            LocalDate.of(2024, 4, 10), LocalDate.of(2024, 4, 20));
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroFechaExactaCumple() {
    FiltroFechaExacta filtro = new FiltroFechaExacta(LocalDate.of(2024, 4, 15));
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroTituloCumple() {
    FiltroTitulo filtro = new FiltroTitulo("Inundación");
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroMultimediaCumple() {
    FiltroMultimedia filtro = new FiltroMultimedia(TipoContenido.IMAGEN);
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroTipoOrigenCumple() {
    FiltroTipoOrigen filtro = new FiltroTipoOrigen(TipoOrigen.CONTRIBUYENTE);
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroCargaEntreCumple() {
    FiltroCargaEntre filtro = new FiltroCargaEntre(
            LocalDateTime.of(2024, 4, 15, 0, 0),
            LocalDateTime.of(2024, 4, 17, 0, 0));
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroUbicacionCumple() {
    FiltroUbicacion filtro = new FiltroUbicacion(new Ubicacion(-34.61, -58.38), 5.0);
    assertThat(filtro.cumpleCon(hecho)).isTrue();
  }

  @Test
  void filtroUbicacionNoCumple() {
    FiltroUbicacion filtro = new FiltroUbicacion(new Ubicacion(-30.0, -50.0), 5.0);
    assertThat(filtro.cumpleCon(hecho)).isFalse();
  }
}
