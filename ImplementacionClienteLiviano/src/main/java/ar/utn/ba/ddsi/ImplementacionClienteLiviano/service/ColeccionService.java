package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ColeccionDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.ColeccionSimpleDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ColeccionService {

  /**
   * Obtiene todas las colecciones disponibles para el panel de administración
   * En una implementación real, esto consultaría al microservicio correspondiente
   */
  public List<ColeccionSimpleDTO> obtenerColeccionesParaAdmin() {
    return Arrays.asList(
            new ColeccionSimpleDTO(1L, "Eventos Históricos", "Colección de eventos históricos relevantes", "eventos-historicos"),
            new ColeccionSimpleDTO(2L, "Noticias Actuales", "Colección de noticias y eventos actuales", "noticias-actuales"),
            new ColeccionSimpleDTO(3L, "Datos Científicos", "Colección de datos y descubrimientos científicos", "datos-cientificos")
    );
  }


  public List<ColeccionDTO> obtenerColeccionesCompletas() {
    throw new UnsupportedOperationException("Funcionalidad pendiente de implementación");
  }
}
