package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Ubicacion")
public class FiltroUbicacion extends Filtro {
  @Transient
  private static Double radioPorDefecto = 10.0;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "latitud", column = @Column(name = "latitud")),
          @AttributeOverride(name = "longitud", column = @Column(name = "longitud"))
  })
  private Ubicacion centro;

  @Column(name = "radio")
  private Double radioEnKm;

  public FiltroUbicacion(Ubicacion centro, Double radioEnKm) {
    super("Ubicacion");
    this.centro = centro;
    this.radioEnKm = radioEnKm == null ? radioPorDefecto : radioEnKm;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    double distancia = centro.distanciaA(hecho.getUbicacion());
    return distancia <= radioEnKm;
  }

  @Override
  public List<String> parametros() {
    return List.of(centro.getLatitud().toString(), centro.getLongitud().toString(), radioEnKm.toString());
  }
}
