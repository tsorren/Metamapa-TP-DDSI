package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para solicitudes de modificación.
 * Representa la respuesta del backend al crear una solicitud de modificación.
 * IMPORTANTE: El campo se llama "modificacion" para coincidir con el JSON del backend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDeModificacionOutputDTO {
    private Long identificador;
    private LocalDateTime fechaCreacion;
    private List<EstadoRevisionOutputDTO> estados; // Backend devuelve "estados" no "estadosDTO"
    private HechoOutputDTO hecho;
    private HechoModificacionDTO modificacion; // Backend devuelve "modificacion" no "hechoModificacion"
    private ContribuyenteOutputDTO contribuyente;
}
