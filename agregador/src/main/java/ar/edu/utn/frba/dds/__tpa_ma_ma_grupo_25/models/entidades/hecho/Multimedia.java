package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.MultimediaOutputDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "multimedia")
public class Multimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_contenido")
  private TipoContenido tipoContenido;

  @Column(name = "url", nullable = false)
  private String url;

  public MultimediaOutputDTO toDTO() {
    return new MultimediaOutputDTO(this.id, this.tipoContenido, this.url);
  }
}
