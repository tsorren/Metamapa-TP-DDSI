package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.util.List;
import java.util.function.Function;

public class Normalizador<T> {

  private final List<String> valoresExistentes;
  private final Function<String, T> buscadorPorNombre;
  private final Function<T, String> extractorDeNombre;
  private final Function<String, T> creadorDeNuevoValor;
  private final float umbralSimilitud;

  public Normalizador(List<String> valoresExistentes,
                      Function<String, T> buscadorPorNombre,
                      Function<T, String> extractorDeNombre,
                      Function<String, T> creadorDeNuevoValor,
                      float umbralSimilitud) {
    this.valoresExistentes = valoresExistentes;
    this.buscadorPorNombre = buscadorPorNombre;
    this.extractorDeNombre = extractorDeNombre;
    this.creadorDeNuevoValor = creadorDeNuevoValor;
    this.umbralSimilitud = umbralSimilitud;
  }

  public T normalizar(T entrada) {
    if (entrada == null) return null;

    String nombreEntrada = extractorDeNombre.apply(entrada);
    if (nombreEntrada == null) return entrada;

    nombreEntrada = nombreEntrada.trim();

    // Paso 1: Buscar coincidencia exacta case-insensitive
    for (String valorExistente : valoresExistentes) {
      if (valorExistente.equalsIgnoreCase(nombreEntrada)) {
        // Encontró la misma palabra con diferente capitalización -> usar la existente
        return buscadorPorNombre.apply(valorExistente);
      }
    }

    // Paso 2: Buscar similitud con Jaro-Winkler (para typos y variaciones)
    StringMetric metric = StringMetrics.jaroWinkler();
    String mejorCoincidencia = null;
    float mejorSimilitud = -1;

    for (String valor : valoresExistentes) {
      float similitud = metric.compare(nombreEntrada.toLowerCase(), valor.toLowerCase());
      if (similitud > mejorSimilitud) {
        mejorSimilitud = similitud;
        mejorCoincidencia = valor;
      }
    }

    if (mejorSimilitud >= umbralSimilitud && mejorCoincidencia != null) {
      return buscadorPorNombre.apply(mejorCoincidencia);
    } else {
      // No existe -> crear nueva con formato capitalizado
      String nombreCanonical = capitalizarPrimeraLetra(nombreEntrada);
      valoresExistentes.add(nombreCanonical);
      return creadorDeNuevoValor.apply(nombreCanonical);
    }
  }

  /**
   * Capitaliza la primera letra de cada palabra para mantener consistencia.
   * Ejemplo: "incendio forestal" -> "Incendio Forestal"
   */
  private String capitalizarPrimeraLetra(String texto) {
    if (texto == null || texto.isEmpty()) return texto;

    String[] palabras = texto.toLowerCase().split("\\s+");
    StringBuilder resultado = new StringBuilder();

    for (int i = 0; i < palabras.length; i++) {
      String palabra = palabras[i];
      if (!palabra.isEmpty()) {
        resultado.append(Character.toUpperCase(palabra.charAt(0)));
        if (palabra.length() > 1) {
          resultado.append(palabra.substring(1));
        }
      }
      if (i < palabras.length - 1) {
        resultado.append(" ");
      }
    }

    return resultado.toString();
  }
}
