package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteEstaticaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.FuenteDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.HechoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class FuenteEstaticaService {

  private final FuenteEstaticaClient fuenteEstaticaClient;

  public FuenteEstaticaService(FuenteEstaticaClient fuenteEstaticaClient) {
    this.fuenteEstaticaClient = fuenteEstaticaClient;
  }

  public List<FuenteDTO> obtenerTodasLasFuentes() {
    return fuenteEstaticaClient.obtenerFuentes(null);
  }

  public List<FuenteDTO> obtenerFuentesDesde(LocalDateTime fecha) {
    return fuenteEstaticaClient.obtenerFuentes(fecha);
  }

  /**
   * Sube el archivo directamente a FuenteEstatica y obtiene los hechos procesados
   */
  public List<HechoDTO> importarHechos(MultipartFile archivo) throws IOException {
    // Usar el nuevo método que sube directamente el archivo
    ResponseEntity<Map> response = fuenteEstaticaClient.subirYProcesarCSV(archivo);

    if (response.getBody() != null && response.getBody().containsKey("identificador")) {
      Long fuenteId = ((Number) response.getBody().get("identificador")).longValue();

      // Los hechos ya se procesan automáticamente en FuenteEstatica cuando se crea la fuente
      // Solo necesitamos obtenerlos de la fuente creada
      return fuenteEstaticaClient.obtenerHechosDeFuente(fuenteId, null);
    }

    throw new RuntimeException("Error al crear la fuente estática");
  }

  /**
   * Inicia la importación y retorna el ID de la fuente inmediatamente
   * La importación se procesa de forma asíncrona en el backend
   */
  public Long iniciarImportacion(MultipartFile archivo) throws IOException {
    ResponseEntity<Map> response = fuenteEstaticaClient.subirYProcesarCSV(archivo);

    if (response.getBody() != null && response.getBody().containsKey("identificador")) {
      return ((Number) response.getBody().get("identificador")).longValue();
    }

    throw new RuntimeException("Error al crear la fuente estática");
  }

  public List<HechoDTO> obtenerHechosDeFuente(Long idFuente) {
    return fuenteEstaticaClient.obtenerHechosDeFuente(idFuente, null);
  }

  public List<HechoDTO> obtenerHechosRecientesDeFuente(Long idFuente, LocalDateTime desde) {
    return fuenteEstaticaClient.obtenerHechosDeFuente(idFuente, desde);
  }

  public void ocultarHecho(Long idHecho) {
    fuenteEstaticaClient.ocultarHecho(idHecho);
  }
}
