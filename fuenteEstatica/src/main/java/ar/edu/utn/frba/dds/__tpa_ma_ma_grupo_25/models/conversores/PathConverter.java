package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.conversores;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Converter(autoApply = true)
public class PathConverter implements AttributeConverter<Path, String> {
  @Override
  public String convertToDatabaseColumn(Path path) {
    return path.toString();
  }

  @Override
  public Path convertToEntityAttribute(String s) {
    return Paths.get(s);
  }
}
