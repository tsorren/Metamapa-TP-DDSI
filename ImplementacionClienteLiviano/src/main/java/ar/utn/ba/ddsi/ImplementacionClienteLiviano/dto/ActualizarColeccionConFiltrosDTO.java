package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO para actualizar una colección existente con nuevos filtros.
 * Permite modificar título, descripción, fuentes, algoritmo y los filtros del criterio.
 */
@Data
public class ActualizarColeccionConFiltrosDTO {
  private String titulo;
  private String descripcion;
  private List<FiltroInputDTO> filtros; // Nuevos filtros que reemplazarán los existentes
  private List<Long> idsFuentes;
  private TipoConsenso algoritmoConsenso;
}

