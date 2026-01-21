package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.lectores.LectorCsv;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FuenteEstaticaTest {

  @Test
  public void testImportarHechosDesdeCsv() {
    FuenteEstatica fuente = new FuenteEstatica(Path.of("fake/path.csv"));

    LectorCsv lectorMock = new LectorCsv() {
      @Override
      public List<HashMap<String, String>> leer(Path rutaArchivo) {
        HashMap<String, String> fila = new HashMap<>();
        fila.put("Título", "Corte de luz");
        fila.put("Descripción", "Zona sin electricidad");
        fila.put("Categoría", "Servicios");
        fila.put("Latitud", "-34.6037");
        fila.put("Longitud", "-58.3816");
        fila.put("Fecha del hecho", "15/06/2024");

        return List.of(fila);
      }
    };

    List<Hecho> hechos = fuente.importarHechos(lectorMock);

    assertEquals(1, hechos.size());
    Hecho hecho = hechos.get(0);

    assertEquals("Corte de luz", hecho.getTitulo());
    assertEquals("Zona sin electricidad", hecho.getDescripcion());
    assertEquals("Servicios", hecho.getCategoria().getNombre());
    assertEquals(LocalDate.of(2024, 6, 15), hecho.getFechaDelHecho());
    assertEquals(TipoOrigen.DATASET, hecho.getOrigen());
    assertEquals(-34.6037, hecho.getUbicacion().getLatitud());
    assertEquals(-58.3816, hecho.getUbicacion().getLongitud());
  }
}
