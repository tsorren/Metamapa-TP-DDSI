package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.UbicacionOutputDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Ubicacion {

  @Transient
  private static final Double RADIO_TIERRA_KM = 6371.0;

  @Column(name = "latitud")
  private Double latitud;

  @Column(name = "longitud")
  private Double longitud;

  public Double distanciaA(Ubicacion otro) {
    double lat1Rad = Math.toRadians(this.latitud);
    double lon1Rad = Math.toRadians(this.longitud);
    double lat2Rad = Math.toRadians(otro.latitud);
    double lon2Rad = Math.toRadians(otro.longitud);

    double deltaLat = lat2Rad - lat1Rad;
    double deltaLon = lon2Rad - lon1Rad;

    double a = Math.pow(Math.sin(deltaLat / 2), 2)
            + Math.cos(lat1Rad) * Math.cos(lat2Rad)
            * Math.pow(Math.sin(deltaLon / 2), 2);

    double c = 2 * Math.asin(Math.sqrt(a));
    return RADIO_TIERRA_KM * c;
  }

  public UbicacionOutputDTO toDTO() {
    return new UbicacionOutputDTO(this.latitud, this.longitud);
  }
}
