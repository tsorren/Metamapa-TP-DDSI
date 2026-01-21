package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Curado")
public class FiltroCurado extends Filtro {
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_consenso")
  private TipoConsenso consenso;

  public FiltroCurado(TipoConsenso consenso) {
    super("Curado");
    this.consenso = consenso;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return hecho.cumple(consenso);
  }

  @Override
  public List<String> parametros() {
    return List.of(consenso.toString());
  }
}
