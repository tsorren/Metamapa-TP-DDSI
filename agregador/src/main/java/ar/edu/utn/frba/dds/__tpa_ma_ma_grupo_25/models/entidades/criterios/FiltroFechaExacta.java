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
@DiscriminatorValue("Fecha_Exacta")
public class FiltroFechaExacta extends Filtro {
  @Column(name = "fecha")
  private LocalDate fecha;

  public FiltroFechaExacta(LocalDate fecha) {
    super("FechaExacta");
    this.fecha = fecha;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return this.fecha.equals(hecho.getFechaDelHecho());
  }

  @Override
  public List<String> parametros() {
    return List.of(fecha.toString());
  }
}
