package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxy;

import java.util.List;

public interface IFuenteProxyRepositorio {
  void guardar(FuenteProxy fuente);

  List<FuenteProxy> encontrarTodas();

  FuenteProxy encontrarPorId(Long identificador);
}
