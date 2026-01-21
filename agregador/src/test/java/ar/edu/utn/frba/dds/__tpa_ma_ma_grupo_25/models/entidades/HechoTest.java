package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HechoTest {

  private Categoria categoriaSalud;
  private Ubicacion ubicacionCABA;
  private Multimedia multimediaAudio;
  private Etiqueta etiquetaPrueba;
  private LocalDate fechaDelHecho;
  private LocalDateTime fechaDeCarga;
  private LocalDateTime fechaUltimaModificacion;

  @BeforeEach
  void setup() {
    categoriaSalud = new Categoria("Salud");
    ubicacionCABA = new Ubicacion(-34.6037, -58.3816);
    multimediaAudio = new Multimedia(1L, TipoContenido.AUDIO, "url_audio");
    etiquetaPrueba = new Etiqueta("prueba");
    fechaDelHecho = LocalDate.of(2023, 6, 1);
    fechaDeCarga = LocalDateTime.now().minusDays(1);
    fechaUltimaModificacion = LocalDateTime.now();
  }

  @Test
  void crearHecho_conParametrosCorrectos_cargaCorrecta() {
    Hecho hecho = Hecho.crear(
            1L,
            "Título Prueba",
            "Descripción de prueba",
            categoriaSalud,
            ubicacionCABA,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            multimediaAudio,
            TipoOrigen.EXTERNO,
            List.of(etiquetaPrueba),
            true
    );

    assertNotNull(hecho);
    assertNull(hecho.getIdentificador()); // siempre null en el crear
    assertEquals(1L, hecho.getIdentificadorExterno());
    assertEquals("Título Prueba", hecho.getTitulo());
    assertEquals("Descripción de prueba", hecho.getDescripcion());
    assertEquals(categoriaSalud, hecho.getCategoria());
    assertEquals(ubicacionCABA, hecho.getUbicacion());
    assertEquals(fechaDelHecho, hecho.getFechaDelHecho());
    assertEquals(fechaDeCarga, hecho.getFechaDeCarga());
    assertEquals(fechaUltimaModificacion, hecho.getFechaUltimaModificacion());
    assertEquals(multimediaAudio, hecho.getContenido());
    assertEquals(TipoOrigen.EXTERNO, hecho.getOrigen());
    assertEquals(1, hecho.getEtiquetas().size());
    assertEquals(etiquetaPrueba, hecho.getEtiquetas().get(0));
    assertTrue(hecho.getVisible());
    assertNull(hecho.getFuente());
  }

  @Test
  void agregarEtiqueta_nuevaEtiqueta_listaIncrementa() {
    Hecho hecho = Hecho.crear(
            2L,
            "Título 2",
            "Desc 2",
            categoriaSalud,
            ubicacionCABA,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            multimediaAudio,
            TipoOrigen.EXTERNO,
            new java.util.ArrayList<>(),
            true
    );

    Etiqueta nuevaEtiqueta = new Etiqueta("nueva");
    hecho.agregarEtiqueta(nuevaEtiqueta);

    assertEquals(1, hecho.getEtiquetas().size());
    assertEquals(nuevaEtiqueta, hecho.getEtiquetas().get(0));
  }

  @Test
  void ubicacion_distanciaCorrecta_calculoHaversine() {
    Ubicacion loc1 = new Ubicacion(-34.6037, -58.3816); // CABA
    Ubicacion loc2 = new Ubicacion(-31.4201, -64.1888); // Córdoba

    double distancia = loc1.distanciaA(loc2);

    // Aproximadamente 695 km entre CABA y Córdoba, tolerancia 10 km
    assertTrue(distancia > 680 && distancia < 710);
  }

  @Test
  void crearHecho_listaEtiquetasVacia_aceptaCorrectamente() {
    Hecho hecho = Hecho.crear(
            3L,
            "Título vacío",
            "Desc vacía",
            categoriaSalud,
            ubicacionCABA,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            multimediaAudio,
            TipoOrigen.EXTERNO,
            List.of(),
            false
    );

    assertNotNull(hecho);
    assertTrue(hecho.getEtiquetas().isEmpty());
    assertFalse(hecho.getVisible());
  }

  @Test
  void crearHecho_conContenidoNulo_aceptaSinError() {
    Hecho hecho = Hecho.crear(
            4L,
            "Sin contenido",
            "Sin contenido multimedia",
            categoriaSalud,
            ubicacionCABA,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            null,
            TipoOrigen.EXTERNO,
            List.of(etiquetaPrueba),
            true
    );

    assertNull(hecho.getContenido());
  }

  @Test
  void crearHecho_conOrigenDiferente_funcionaCorrectamente() {
    Hecho hecho = Hecho.crear(
            5L,
            "Origen diferente",
            "Con origen estático",
            categoriaSalud,
            ubicacionCABA,
            fechaDelHecho,
            fechaDeCarga,
            fechaUltimaModificacion,
            multimediaAudio,
            TipoOrigen.MANUAL,
            List.of(etiquetaPrueba),
            true
    );

    assertEquals(TipoOrigen.MANUAL, hecho.getOrigen());
  }
}
