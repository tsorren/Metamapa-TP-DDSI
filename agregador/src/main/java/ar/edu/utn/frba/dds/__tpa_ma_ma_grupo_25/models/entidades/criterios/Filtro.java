package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FiltroOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Filtro {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "tipo")
  protected String tipo;

  public Filtro(String tipo) {
    this.tipo = tipo;
  }

  abstract boolean cumpleCon(Hecho hecho);

  public abstract List<String> parametros();

  public FiltroOutputDTO toDTO() {
    return new FiltroOutputDTO(this.id, this.tipo, this.parametros());
  }
}
