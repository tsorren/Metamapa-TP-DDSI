package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEliminacionInputDTO {
    private Long idHecho;
    private String justificacion;
    private Long idContribuyente;
}
