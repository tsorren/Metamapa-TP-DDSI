package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ModuloFuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;

import java.time.LocalDateTime;
import java.util.List;

public interface IModuloFuenteServicio {
  ModuloFuenteOutputDTO crear(TipoFuente tipo, String url);

  List<ModuloFuenteOutputDTO> encontrarTodas();

  void cargarFuentes(ModuloFuente modulo, LocalDateTime ultimoRefresco);

  void pedirNuevasFuentes(LocalDateTime ultimoRefresco);

  ModuloFuente findByTipoFuente(TipoFuente tipoFuente);
}
