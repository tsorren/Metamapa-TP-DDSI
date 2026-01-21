package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HechoTest {

  @Test
  void seCreaCorrectamenteConMetodoCrearBasico() {
    Categoria categoria = new Categoria("Ambiental");
    Ubicacion ubicacion = new Ubicacion(-34.6037, -58.3816);
    LocalDate fecha = LocalDate.of(2023, 10, 5);
    Hecho hecho = Hecho.crear("Contaminación", "Vertido en río", categoria, ubicacion, fecha, TipoOrigen.CONTRIBUYENTE);

    assertNull(hecho.getIdentificador());
    assertEquals("Contaminación", hecho.getTitulo());
    assertEquals("Vertido en río", hecho.getDescripcion());
    assertEquals("Ambiental", hecho.getCategoria().getNombre());
    assertEquals(ubicacion, hecho.getUbicacion());
    assertEquals(fecha, hecho.getFechaDelHecho());
    assertNotNull(hecho.getFechaDeCarga());
    assertNotNull(hecho.getFechaUltimaModificacion());
    assertEquals(TipoOrigen.CONTRIBUYENTE, hecho.getOrigen());
    assertNull(hecho.getContenido());
    assertTrue(hecho.getEtiquetas().isEmpty());
    assertTrue(hecho.getVisible());
  }

  @Test
  void seCreaCorrectamenteConContenido() {
    Categoria categoria = new Categoria("Social");
    Ubicacion ubicacion = new Ubicacion(40.4168, -3.7038);
    LocalDate fecha = LocalDate.of(2022, 8, 12);
    Multimedia multimedia = new Multimedia(1L, TipoContenido.IMAGEN, "https://ejemplo.com/img.png");

    Hecho hecho = Hecho.crear("Evento", "Concentración social", categoria, ubicacion, fecha, TipoOrigen.MANUAL);

    assertEquals("Social", hecho.getCategoria().getNombre());
    assertEquals(multimedia, hecho.getContenido());
    assertEquals(TipoContenido.IMAGEN, hecho.getContenido().getTipoContenido());
  }

  @Test
  void seCreaCorrectamenteConVisibilidadEspecifica() {
    Categoria categoria = new Categoria("Seguridad");
    Ubicacion ubicacion = new Ubicacion(35.6895, 139.6917);
    LocalDate fecha = LocalDate.of(2021, 4, 15);
    Multimedia multimedia = new Multimedia(2L, TipoContenido.VIDEO, "https://ejemplo.com/video.mp4");

    Hecho hecho = Hecho.crear("Robo", "Incidente registrado", categoria, ubicacion, fecha, TipoOrigen.DATASET);
    hecho.ocultar();
    assertFalse(hecho.getVisible());
    assertEquals(TipoOrigen.DATASET, hecho.getOrigen());
  }

  @Test
  void puedoAgregarEtiquetasCorrectamente() {
    Hecho hecho = Hecho.crear("Incendio", "Fuego en parque", new Categoria("Emergencia"),
            new Ubicacion(10.0, 20.0), LocalDate.now(), TipoOrigen.CONTRIBUYENTE);

    Etiqueta etiqueta1 = new Etiqueta("urgente");
    Etiqueta etiqueta2 = new Etiqueta("bosque");

    hecho.agregarEtiqueta(etiqueta1);
    hecho.agregarEtiqueta(etiqueta2);

    assertEquals(2, hecho.getEtiquetas().size());
    assertTrue(hecho.getEtiquetas().contains(etiqueta1));
    assertTrue(hecho.getEtiquetas().contains(etiqueta2));
  }

  @Test
  void fechasDeCargaYUltimaModificacionSeAsignanAutomaticamente() {
    Hecho hecho = Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL);

    LocalDateTime ahora = LocalDateTime.now();
    assertNotNull(hecho.getFechaDeCarga());
    assertNotNull(hecho.getFechaUltimaModificacion());
    assertFalse(hecho.getFechaDeCarga().isAfter(ahora));
    assertFalse(hecho.getFechaUltimaModificacion().isAfter(ahora));
  }
}
