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
@DiscriminatorValue("Titulo")
public class FiltroTitulo extends Filtro {
  @Column(name = "titulo")
  private String titulo;

  public FiltroTitulo(String titulo) {
    super("Titulo");
    this.titulo = titulo;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    return this.titulo.equals(hecho.getTitulo());
  }

  @Override
  public List<String> parametros() {
    return List.of(titulo);
  }
}
