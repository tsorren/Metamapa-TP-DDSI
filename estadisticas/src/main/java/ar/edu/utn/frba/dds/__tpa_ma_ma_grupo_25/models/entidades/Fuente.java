package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "fuentes_dinamicas")
public class Fuente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_de_creacion")
  private LocalDateTime fechaDeCreacion = LocalDateTime.now();

  // ✅ ManyToMany bidireccional con Hecho
  @ManyToMany
  @JoinTable(
          name = "fuente_hecho",
          joinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  private List<Hecho> hechosAsociados = new ArrayList<>();

}
