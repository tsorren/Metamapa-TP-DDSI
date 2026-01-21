package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Entity
@Table(name = "fuentes_dinamicas")
@Data
public class FuenteDinamica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_de_creacion")
  private LocalDateTime fechaDeCreacion = LocalDateTime.now();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "fuente_hecho",
          joinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "hecho_id", referencedColumnName = "id")
  )
  private List<Hecho> hechosAsociados = new ArrayList<>();

  public FuenteDinamicaOutputDTO toDTO() {
      return new FuenteDinamicaOutputDTO(
              id,
              fechaDeCreacion//,
              //f.getHechosAsociados()
      );
  }
}

