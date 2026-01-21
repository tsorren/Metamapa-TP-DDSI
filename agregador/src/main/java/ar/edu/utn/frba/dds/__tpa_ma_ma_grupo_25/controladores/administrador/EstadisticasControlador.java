package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasAgregadorDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEstadisticasServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class EstadisticasControlador {

    private final IEstadisticasServicio estadisticasServicio;

    public EstadisticasControlador(IEstadisticasServicio estadisticasServicio) {
        this.estadisticasServicio = estadisticasServicio;
    }

    @GetMapping("/estadisticas")
    public EstadisticasAgregadorDTO obtenerEstadisticas() {
        log.info("<REQUEST> Solicitando estadísticas del Agregador");
        return estadisticasServicio.obtenerEstadisticas();
    }
}

