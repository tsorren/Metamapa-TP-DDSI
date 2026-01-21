package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.lectores.LectorCsv;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LectorCsvTest {

  @Test
  public void testLecturaBasicaDeCsv() throws Exception {
    Path archivoTemporal = Files.createTempFile("hechos_test", ".csv");
    try (FileWriter writer = new FileWriter(archivoTemporal.toFile())) {
      writer.write("Título,Descripción,Categoría,Latitud,Longitud,Fecha del hecho\n");
      writer.write("Incendio,Calle falsa 123,Emergencia,-34.60,-58.39,16/06/2024\n");
    }

    LectorCsv lector = new LectorCsv();
    List<HashMap<String, String>> filas = lector.leer(archivoTemporal);

    assertEquals(1, filas.size());

    HashMap<String, String> fila = filas.get(0);
    assertEquals("Incendio", fila.get("Título"));
    assertEquals("Calle falsa 123", fila.get("Descripción"));
    assertEquals("Emergencia", fila.get("Categoría"));
    assertEquals("-34.60", fila.get("Latitud"));
    assertEquals("-58.39", fila.get("Longitud"));
    assertEquals("16/06/2024", fila.get("Fecha del hecho"));
  }
}
