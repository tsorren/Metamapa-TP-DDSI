package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

@Data
public class ColeccionSimpleDTO {
  private Long id;
  private String nombre;
  private String descripcion;
  private String handle;

  public ColeccionSimpleDTO(Long id, String nombre, String descripcion, String handle) {
    this.id = id;
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.handle = handle;
  }
}
