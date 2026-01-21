package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasFuenteDinamicaDTO {
    private Long hechosCreados;  // Total de hechos creados (solicitudes de creación aceptadas)
    private Long hechosModificados;  // Total de hechos modificados (solicitudes de modificación aceptadas)
    private Long solicitudesCreacionTotal;
    private Long solicitudesCreacionAceptadas;
    private Long solicitudesCreacionRechazadas;
    private Long solicitudesCreacionPendientes;
    private Long solicitudesCreacionPendientesModificacion;
    private Long solicitudesModificacionTotal;
    private Long solicitudesModificacionAceptadas;
    private Long solicitudesModificacionRechazadas;
    private Long solicitudesModificacionPendientes;
    private Long solicitudesModificacionPendientesModificacion;
}

