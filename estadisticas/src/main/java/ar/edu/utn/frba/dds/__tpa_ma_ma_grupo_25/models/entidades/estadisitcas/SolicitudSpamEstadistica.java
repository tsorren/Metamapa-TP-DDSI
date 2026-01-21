package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "solicitud_spam_estadistica")
public class SolicitudSpamEstadistica {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "cantidad_spam")
  private Long cantidadSpam;

  @Column(name = "fecha_calculo")
  private LocalDateTime fechaCalculo;

}
