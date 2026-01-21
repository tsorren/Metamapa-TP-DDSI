package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoContenido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Multimedia")
public class FiltroMultimedia extends Filtro {
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_contenido")
  private TipoContenido tipoContenido;

  public FiltroMultimedia(TipoContenido tipoContenido) {
    super("Multimedia");
    this.tipoContenido = tipoContenido;

  }

  public boolean cumpleCon(Hecho hecho) {
    return hecho.getContenido() != null && tipoContenido.equals(hecho.getContenido().getTipoContenido());
  }

  @Override
  public List<String> parametros() {
    return List.of(tipoContenido.name());
  }
}
