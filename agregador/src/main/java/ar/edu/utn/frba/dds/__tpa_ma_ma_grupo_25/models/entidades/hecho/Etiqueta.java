package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EtiquetaOutputDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "etiquetas")
public class Etiqueta {
  @Column(name = "nombre")
  private String nombre;

  @ManyToMany(mappedBy = "etiquetas")
  private List<Hecho> hechos;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }

  public EtiquetaOutputDTO toDTO() {
    return new EtiquetaOutputDTO(this.id, this.nombre);
  }
}
