package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IDetectorDeSpam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DetectorDeSpam implements IDetectorDeSpam {
  private final List<String> PALABRAS_SPAM;
  private final int UMBRAL_SPAM;
  private final int caracteresMinimos;

  public DetectorDeSpam(@Value("${filtro.spam.palabras}") String palabras, @Value("${filtro.spam.umbral}") int umbral) {
    this.PALABRAS_SPAM = Arrays.asList(palabras.split("\\s*,\\s*"));
    this.UMBRAL_SPAM = umbral;
    this.caracteresMinimos = 500;
  }

  @Override
  public boolean esSpam(String texto) {
    if (texto.length() < caracteresMinimos) {
      return true;
    }

    String limpio = limpiarTexto(texto);
    List<String> palabrasTexto = Arrays.asList(limpio.split(" "));
    Map<String, Double> tf = calcularTF(palabrasTexto);
    Map<String, Double> idf = calcularIDF();

    // Calcular TF-IDF para palabras spam
    double puntajeTFIDF = PALABRAS_SPAM.stream()
            .filter(tf::containsKey)
            .mapToDouble(p -> tf.get(p) * idf.getOrDefault(p, 0.0))
            .sum();

    // Penalizaciones adicionales por signos o URLs
    if (limpio.matches(".*(https?://|www\\.).*")) puntajeTFIDF += 1.0;
    if (limpio.matches(".*[!¡]{3,}.*")) puntajeTFIDF += 1.0;

    return puntajeTFIDF >= UMBRAL_SPAM;
  }

  private Map<String, Double> calcularTF(List<String> palabras) {
    Map<String, Long> frecuencias = palabras.stream()
            .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

    double total = palabras.size();
    Map<String, Double> tf = new HashMap<>();
    for (Map.Entry<String, Long> entrada : frecuencias.entrySet()) {
      tf.put(entrada.getKey(), entrada.getValue() / total);
    }
    return tf;
  }

  private Map<String, Double> calcularIDF() {
    // Consideramos cada palabra de PALABRAS_SPAM como un documento individual donde aparece 1 vez
    int totalDocs = PALABRAS_SPAM.size();
    Map<String, Integer> docFreq = new HashMap<>();

    for (String palabra : PALABRAS_SPAM) {
      docFreq.put(palabra, 1); // aparece en un documento
    }

    Map<String, Double> idf = new HashMap<>();
    for (String palabra : PALABRAS_SPAM) {
      double df = docFreq.getOrDefault(palabra, 1);
      idf.put(palabra, Math.log((double) totalDocs / df)); // log(N/df)
    }
    return idf;
  }

  private String limpiarTexto(String texto) {
    String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return sinTildes.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", " ")
            .replaceAll("\\s+", " ")
            .trim();
  }
}
