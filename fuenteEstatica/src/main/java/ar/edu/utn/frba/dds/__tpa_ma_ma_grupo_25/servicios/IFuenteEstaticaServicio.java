package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteEstaticaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface IFuenteEstaticaServicio {
  FuenteOutputDTO crearFuente(Path ruta);
  ResponseEntity<FuenteOutputDTO> subirYCrearFuente(MultipartFile archivo);
  List<FuenteOutputDTO> encontrarTodas();
  List<HechoOutputDTO> importarHechos(Long identificador);
  List<HechoOutputDTO> hechosDeFuente(Long identificador);
  EstadisticasFuenteEstaticaDTO obtenerEstadisticas();
}
