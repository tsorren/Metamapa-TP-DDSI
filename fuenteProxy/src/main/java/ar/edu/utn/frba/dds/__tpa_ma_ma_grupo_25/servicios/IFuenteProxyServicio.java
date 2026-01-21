package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteProxyDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteProxyOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IFuenteProxyServicio {
  FuenteProxyOutputDTO crear(String url);
  FuenteProxyOutputDTO crearFuenteRecursiva(String url, String nombre);
  FuenteProxyOutputDTO crearFuenteExterna(String url, String email, String password, String nombre);
  List<FuenteProxyOutputDTO> encontrarFuentes();
  List<HechoOutputDTO> encontrarHechos(Long idFuente, LocalDateTime fechaAnteriorA);
  HechoOutputDTO encontrarHechoPorId(Long idFuente, Long idHecho);
  EstadisticasFuenteProxyDTO obtenerEstadisticas();
}
