package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import lombok.Data;

@Data
public class Etiqueta {
  private final String nombre;
  private Long identificador;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}
