package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasDTO {
    // Estadísticas de Hechos
    private Long hechosCargados;  // Total de hechos desde fuente proxy
    private Long hechosImportados;  // Total de hechos en fuente estática (importados de CSV)
    private Long hechosCreados;  // Total de hechos creados (aceptados en fuente dinámica)
    private Long hechosModificados;  // Total de hechos modificados (aceptados en fuente dinámica)

    // Estadísticas de Colecciones
    private Long coleccionesCreadas;  // Total de colecciones en agregador
    private Long coleccionesModificadas;  // Total de modificaciones en colecciones

    // Estadísticas de Fuentes
    private Long fuentesActivas;  // Total de fuentes activas en agregador

    // Estadísticas de Solicitudes
    private Long solicitudesEliminacion;  // Total de solicitudes de eliminación
    private Long solicitudesCreacion;  // Total de solicitudes de creación
    private Long solicitudesModificacion;  // Total de solicitudes de modificación
    private Long solicitudesAceptadas;  // Total de solicitudes aceptadas (suma de las 3)
    private Long solicitudesRechazadas;  // Total de solicitudes rechazadas (suma de las 3)
    private Long solicitudesPendientes;  // Total de solicitudes pendientes (suma de las 3)
    private Long solicitudesPendientesModificacion;  // Solicitudes en estado PENDIENTE_DE_MODIFICACION

    // Estadísticas de Análisis (del módulo de estadísticas)
    private String provinciaConMasHechos;  // Provincia con más hechos registrados
    private String categoriaConMasHechos;  // Categoría con más hechos registrados
    private Integer totalHechosPorProvincia;  // Total de provincias con hechos
    private Integer totalHechosPorCategoria;  // Total de categorías con hechos
}
