package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Categoria")
public class FiltroCategoria extends Filtro {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", referencedColumnName = "id")
  private Categoria categoria;

  public FiltroCategoria(Categoria categoria) {
    super("Categoria");
    this.categoria = categoria;
  }

  @Override
  public boolean cumpleCon(Hecho hecho) {
    // Comparación case-insensitive para mayor flexibilidad
    if (this.categoria == null || this.categoria.getNombre() == null) {
      return false;
    }
    if (hecho.getCategoria() == null || hecho.getCategoria().getNombre() == null) {
      return false;
    }
    return this.categoria.getNombre().equalsIgnoreCase(hecho.getCategoria().getNombre());
  }

  @Override
  public List<String> parametros() {
    return List.of(categoria.getNombre());
  }
}
