package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Descripcion")
public class FiltroDescripcion extends Filtro {
  @Column(name = "descripcion")
  private String descripcion;

  public FiltroDescripcion(String descripcion) {
    super("Descripcion");
    this.descripcion = descripcion;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return this.descripcion.equals(hecho.getDescripcion());
  }

  @Override
  public List<String> parametros() {
    return List.of(descripcion);
  }
}
