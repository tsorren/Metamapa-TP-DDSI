package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import lombok.Getter;

//clase desarrollada solo para evitar el problema de consistencia de datos
@Getter
public class Categoria {
  private final String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }
}

