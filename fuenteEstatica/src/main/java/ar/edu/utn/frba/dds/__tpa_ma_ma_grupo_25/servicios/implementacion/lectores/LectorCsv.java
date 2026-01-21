package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.lectores;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LectorCsv {
  public List<HashMap<String, String>> leer(Path rutaArchivo) {
    List<HashMap<String, String>> filas = new ArrayList<>();

    try (CSVReader lector = new CSVReader(new FileReader(rutaArchivo.toFile()))) {
      String[] columnas = lector.readNext(); // encabezado

      // Limpiar BOM UTF-8 de la primera columna si existe
      if (columnas.length > 0 && columnas[0] != null) {
        columnas[0] = columnas[0].replace("\uFEFF", ""); // Eliminar BOM
        columnas[0] = columnas[0].trim(); // Eliminar espacios en blanco
      }

      System.out.println("Columnas detectadas: " + String.join(", ", columnas));
      System.out.println("Total de columnas: " + columnas.length);
      System.out.println("Primera columna (sin BOM): '" + columnas[0] + "'");

      String[] valores;
      int lineNumber = 1;

      while ((valores = lector.readNext()) != null) {
        lineNumber++;

        // Ignorar líneas vacías
        if (valores.length == 0 || (valores.length == 1 && valores[0].trim().isEmpty())) {
          System.out.println("Línea " + lineNumber + " vacía, ignorando...");
          continue;
        }

        HashMap<String, String> fila = procesarFila(columnas, valores);

        // Validar que la fila tenga el campo Título
        if (fila.get("Título") == null || fila.get("Título").trim().isEmpty()) {
          System.out.println("Línea " + lineNumber + " sin título válido, ignorando...");
          System.out.println("  Claves disponibles: " + String.join(", ", fila.keySet()));
          System.out.println("  Valor de 'Título': " + fila.get("Título"));
          continue;
        }

        agregarFila(filas, fila);
      }

      System.out.println("Total de filas procesadas exitosamente: " + filas.size());

    } catch (Exception e) {
      System.err.println("Error al leer el archivo CSV: " + e.getMessage());
      e.printStackTrace();
    }

    return filas;
  }


  private Optional<HashMap<String, String>> buscarFilaPorTitulo(List<HashMap<String, String>> filas, HashMap<String, String> fila) {
    String titulo = fila.get("Título");
    if (titulo == null || titulo.isEmpty()) {
      return Optional.empty();
    }

    return filas.stream()
            .filter(f -> {
              String tituloExistente = f.get("Título");
              return tituloExistente != null && tituloExistente.equals(titulo);
            })
            .findFirst();
  }

  private void agregarFila(List<HashMap<String, String>> filas, HashMap<String, String> fila) {
    buscarFilaPorTitulo(filas, fila)
            .ifPresent(filas::remove);
    filas.add(fila);
  }


  private HashMap<String, String> procesarFila(String[] columnas, String[] valores) {
    var fila = new HashMap<String, String>();
    for (int i = 0; i < columnas.length; i++) {
      fila.put(columnas[i], valores[i]);
    }
    return fila;
  }
}