package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteDinamicaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteDinamicaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class EstadisticasControlador {

    private final IFuenteDinamicaServicio fuenteDinamicaServicio;

    public EstadisticasControlador(IFuenteDinamicaServicio fuenteDinamicaServicio) {
        this.fuenteDinamicaServicio = fuenteDinamicaServicio;
    }

    @GetMapping("/estadisticas")
    public EstadisticasFuenteDinamicaDTO obtenerEstadisticas() {
        log.info("<REQUEST> Solicitando estadísticas de FuenteDinamica");
        return fuenteDinamicaServicio.obtenerEstadisticas();
    }
}

