package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Multimedia {
  private Long id;

  // Acepta tanto "tipo" como "tipoContenido" del backend
  @JsonProperty("tipoContenido")
  private TipoContenido tipoContenido;

  private TipoContenido tipo;

  private String url;

  /**
   * Obtener tipo de contenido con fallback por extensión de archivo
   */
  public TipoContenido getTipoContenidoODetectado() {
    // Intentar con tipoContenido primero
    if (tipoContenido != null) {
      return tipoContenido;
    }

    // Luego con tipo
    if (tipo != null) {
      return tipo;
    }

    // Fallback: detectar por extensión de archivo
    if (url != null && !url.isEmpty()) {
      String urlLower = url.toLowerCase();
      if (urlLower.matches(".*\\.(jpg|jpeg|png|gif|bmp|svg|webp).*")) {
        return TipoContenido.IMAGEN;
      } else if (urlLower.matches(".*\\.(mp4|webm|ogv|avi|mov|mkv).*")) {
        return TipoContenido.VIDEO;
      } else if (urlLower.matches(".*\\.(mp3|wav|ogg|m4a|aac|flac).*")) {
        return TipoContenido.AUDIO;
      } else if (urlLower.matches(".*\\.(txt|text).*")) {
        return TipoContenido.TEXTO;
      }
    }

    return null;
  }
}