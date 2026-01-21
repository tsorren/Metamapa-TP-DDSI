package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteServicio;

import java.util.List;

public class ConsensoMultiplesMenciones extends AlgoritmoDeConsenso {

  public ConsensoMultiplesMenciones(FuenteServicio fuenteServicio) {
    super(TipoConsenso.MULTIPLE_MENCION, fuenteServicio);
  }

  @Override
  public boolean cumpleCriterioCurado(Hecho hechoOriginal, List<Hecho> hechoEnFuentes, List<Fuente> fuentes) {
    return hechoEnFuentes.size() > 2 && this.contienenElMismoHecho(hechoOriginal, hechoEnFuentes);
  }
}

