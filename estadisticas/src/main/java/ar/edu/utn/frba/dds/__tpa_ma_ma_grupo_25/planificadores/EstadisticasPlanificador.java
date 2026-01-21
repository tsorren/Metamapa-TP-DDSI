package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.EstadisticaService;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class EstadisticasPlanificador {

  private final EstadisticaService estadisticaService;
  private LocalDateTime ultimoProcesamiento;

  public EstadisticasPlanificador(EstadisticaService estadisticaService) {
    this.estadisticaService = estadisticaService;
    this.ultimoProcesamiento = LocalDateTime.now();
  }

  @Scheduled(cron = "0 0 1 * * *")
  public void recalcularEstadisticas() {
    LocalDateTime nuevoValor = LocalDateTime.now();
    System.out.println("Recalculando estadísticas a la 1 AM...");
    estadisticaService.recalcularEstadisticas();
    ultimoProcesamiento = nuevoValor;
  }
}
