package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.util.List;

public class Normalizador {

  private final StringMetric metric = StringMetrics.jaroWinkler();

  /**
   * Dado un término de entrada y una lista de términos existentes, devuelve el más similar.
   * Si no hay ninguno con similitud >= umbral, devuelve null.
   */
  public String normalizar(String entrada, List<String> existentes, float umbral) {
    if (entrada == null || existentes == null || existentes.isEmpty()) return null;

    String mejorCoincidencia = null;
    float mejorSimilitud = -1;

    for (String candidato : existentes) {
      float similitud = metric.compare(entrada.toLowerCase(), candidato.toLowerCase());
      if (similitud > mejorSimilitud) {
        mejorSimilitud = similitud;
        mejorCoincidencia = candidato;
      }
    }

    return mejorSimilitud >= umbral ? mejorCoincidencia : null;
  }
}
