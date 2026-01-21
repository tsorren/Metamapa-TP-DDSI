package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Etiqueta")
public class Etiqueta {
  @Column(name = "nombre")
  private String nombre;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}
