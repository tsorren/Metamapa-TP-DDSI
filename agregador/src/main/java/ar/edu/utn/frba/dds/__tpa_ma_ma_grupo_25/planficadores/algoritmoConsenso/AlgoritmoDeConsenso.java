package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteServicio;

import java.util.ArrayList;
import java.util.List;


public abstract class AlgoritmoDeConsenso {
  protected TipoConsenso tipoConsenso;
  protected FuenteServicio fuenteServicio;

  public AlgoritmoDeConsenso(TipoConsenso tipoConsenso, FuenteServicio fuenteServicio) {
    this.tipoConsenso = tipoConsenso;
    this.fuenteServicio = fuenteServicio;
  }

  // Template Method
  public void curarHecho(Hecho hecho, List<Fuente> fuentes) {
    if (hecho.cumple(tipoConsenso)) {
      return;
    }
    List<Hecho> hechoEnFuentes = new ArrayList<>(); // De mismo titulo
    fuentes.forEach(fuente ->
            fuenteServicio.encontrarHechoPorTitulo(fuente, hecho.getTitulo())
                    .ifPresent(hechoEnFuentes::add)
    );

    if (this.cumpleCriterioCurado(hecho, hechoEnFuentes, fuentes)) {
      hecho.curar(tipoConsenso);
      hechoEnFuentes.forEach(h -> h.curar(tipoConsenso)); // Esto nos puede generar problemas
    }
  }

  public abstract boolean cumpleCriterioCurado(Hecho hechoOriginal, List<Hecho> hechoEnFuentes, List<Fuente> fuentes);

  protected boolean contienenElMismoHecho(Hecho hechoOriginal, List<Hecho> hechoEnFuentes) {
    return hechoEnFuentes.stream().allMatch(hechoOriginal::tieneMismosAtributos);
  }
}
