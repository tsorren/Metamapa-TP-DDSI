package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Fecha_Entre")
public class FiltroFechaEntre extends Filtro {
  @Column(name = "fecha_inicio")
  private LocalDate fechaInicio;
  @Column(name = "fecha_fin")
  private LocalDate fechaFin;

  public FiltroFechaEntre(LocalDate fechaInicio, LocalDate fechaFin) {
    super("FechaEntre");
    this.fechaInicio = fechaInicio == null ? LocalDate.MIN : fechaInicio;
    this.fechaFin = fechaFin == null ? LocalDate.MAX : fechaFin;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return (hecho.getFechaDelHecho().isEqual(fechaInicio) || hecho.getFechaDelHecho().isAfter(fechaInicio)) &&
            (hecho.getFechaDelHecho().isEqual(fechaFin) || hecho.getFechaDelHecho().isBefore(fechaFin));
  }

  @Override
  public List<String> parametros() {
    return List.of(fechaInicio.toString(), fechaFin.toString());
  }
}
