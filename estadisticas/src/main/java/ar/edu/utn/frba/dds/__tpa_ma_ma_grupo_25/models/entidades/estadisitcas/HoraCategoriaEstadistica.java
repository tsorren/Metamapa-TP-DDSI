package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "hora_categoria_estadistica")
public class HoraCategoriaEstadistica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "categoria")
  private String categoria;

  @Column(name = "hora_del_dia")
  private int horaDelDia;

  @Column(name = "cantidad_hechos")
  private Long cantidadHechos;

  @Column(name = "fecha_calculo")
  private LocalDateTime fechaCalculo;
}
