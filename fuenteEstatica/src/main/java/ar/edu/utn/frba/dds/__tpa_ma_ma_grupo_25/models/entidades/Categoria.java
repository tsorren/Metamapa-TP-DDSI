package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//clase desarrollada solo para evitar el problema de consistencia de datos
@Getter
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }

  public CategoriaOutputDTO toDTO() {
    return new CategoriaOutputDTO(this.id, this.nombre);
  }
}

