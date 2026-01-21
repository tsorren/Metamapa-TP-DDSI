package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "etiquetas")
public class Etiqueta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  // ✅ Bidireccional con Hecho
  @ManyToMany(mappedBy = "etiquetas")
  private List<Hecho> hechos;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }
}
