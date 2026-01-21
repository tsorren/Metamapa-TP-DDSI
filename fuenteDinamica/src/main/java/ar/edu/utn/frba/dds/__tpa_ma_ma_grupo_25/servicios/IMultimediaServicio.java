package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.MultimediaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoContenido;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IMultimediaServicio {
  MultimediaOutputDTO crear(TipoContenido tipoContenido, String url);
  MultimediaOutputDTO encontrarPorId(Long id);
  MultimediaOutputDTO subirArchivo(MultipartFile archivo, TipoContenido tipoContenido) throws IOException;
}
