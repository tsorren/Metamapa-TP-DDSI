package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones;


public class NotFoundException extends RuntimeException {
  public NotFoundException(String entidad, String id) {
    super("No se ha encontrado " + entidad + " de id " + id);
  }
}
