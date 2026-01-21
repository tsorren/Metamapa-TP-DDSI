package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para representar las modificaciones propuestas a un hecho.
 * Usado tanto para input como output en solicitudes de modificación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HechoModificacionDTO {
    private Long idHecho;
    private Long idContribuyente;
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private Ubicacion ubicacion;
    private LocalDate fechaDelHecho;
    private Multimedia contenido;
}
