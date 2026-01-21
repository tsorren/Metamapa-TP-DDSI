package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasAgregadorDTO {
    private Long coleccionesCreadas;
    private Long coleccionesModificadas;
    private Long fuentesActivas;
    private Long solicitudesEliminacionTotal;
    private Long solicitudesEliminacionAceptadas;
    private Long solicitudesEliminacionRechazadas;
    private Long solicitudesEliminacionPendientes;
}

