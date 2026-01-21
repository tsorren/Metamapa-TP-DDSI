package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CategoriaOutputDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

//clase desarrollada solo para evitar el problema de consistencia de datos
@Data
@NoArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {
  @Column(name = "nombre")
  private String nombre;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Categoria(String nombre) {
    this.nombre = nombre;
  }

  public CategoriaOutputDTO toDTO() {
    return new CategoriaOutputDTO(this.id, this.nombre);
  }
}

