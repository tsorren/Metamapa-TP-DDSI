package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Tipo_Origen")
public class FiltroTipoOrigen extends Filtro {
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_origen")
  private TipoOrigen tipoOrigen;

  public FiltroTipoOrigen(TipoOrigen tipoOrigen) {
    super("TipoOrigen");
    this.tipoOrigen = tipoOrigen;
  }

  public boolean cumpleCon(Hecho hecho) {
    return this.tipoOrigen.equals(hecho.getOrigen());
  }

  @Override
  public List<String> parametros() {
    return List.of(tipoOrigen.name());
  }
}
