package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.conversores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.util.ArrayList;
import java.util.List;

//TODO: esto no tendria que ser un conversor, tendria que se ruan comprovacion e nla capa de services o repositorio
@Converter(autoApply = true)
public class CategoriaConverter implements AttributeConverter<Categoria, String> {

  private List<String> categorias;

  public CategoriaConverter() {
    //TODO: cuando el repo de categorias este listo, extrear los titulos de categorias a este array
    this.categorias = new ArrayList<>();
  }

  @Override
  public String convertToDatabaseColumn(Categoria categoria) {
    if (categoria != null) {
      String entrada = categoria.getNombre();

      StringMetric metric = StringMetrics.jaroWinkler();

      String mejorCategoria = null;
      float mejorSimilitud = -1;

      for (String categoriaEx : categorias) {
        float similitud = metric.compare(entrada.toLowerCase(), categoriaEx.toLowerCase());
        if (similitud > mejorSimilitud) {
          mejorSimilitud = similitud;
          mejorCategoria = categoriaEx;
        }
      }

      if (mejorSimilitud >= 0.8) {
        return mejorCategoria;
      } else {
        categorias.add(entrada);
        return entrada;
      }
    } else {
      return null;
    }
  }

  @Override
  public Categoria convertToEntityAttribute(String s) {
    return null;
  }

}
