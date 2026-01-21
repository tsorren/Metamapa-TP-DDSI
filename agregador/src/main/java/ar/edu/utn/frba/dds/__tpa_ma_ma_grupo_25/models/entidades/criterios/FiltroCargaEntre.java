package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Carga_Entre")
public class FiltroCargaEntre extends Filtro {
  @Column(name = "fecha_de_inicio")
  private LocalDateTime fechaInicio;

  @Column(name = "fecha_de_fin")
  private LocalDateTime fechaFin;

  public FiltroCargaEntre(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    super("CargaEntre");
    this.fechaInicio = fechaInicio == null ? LocalDateTime.MIN : fechaInicio;
    this.fechaFin = fechaFin == null ? LocalDateTime.MAX : fechaFin;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return (hecho.getFechaDeCarga().isEqual(fechaInicio) || hecho.getFechaDeCarga().isAfter(fechaInicio)) &&
            (hecho.getFechaDeCarga().isEqual(fechaFin) || hecho.getFechaDeCarga().isBefore(fechaFin));
  }

  @Override
  public List<String> parametros() {
    return List.of(fechaInicio.toString(), fechaFin.toString());
  }

}
