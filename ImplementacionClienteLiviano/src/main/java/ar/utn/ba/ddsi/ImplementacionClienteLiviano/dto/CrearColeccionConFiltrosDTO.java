package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO para crear una colección con criterios de pertenencia dinámicos.
 * En vez de recibir un idCriterio, recibe una lista de filtros que se crearán.
 */
@Data
public class CrearColeccionConFiltrosDTO {
  private String titulo;
  private String descripcion;
  private List<FiltroInputDTO> filtros; // Puede ser vacía
  private List<Long> idsFuentes;
  private TipoConsenso algoritmoConsenso;
}
