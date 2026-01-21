package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class SolicitudesPlanificador {
  private final ISolicitudEliminacionServicio solicitudEliminacionServicio;
  private LocalDateTime ultimoProcesamiento;

  public SolicitudesPlanificador(ISolicitudEliminacionServicio solicitudEliminacionServicio) {
    this.solicitudEliminacionServicio = solicitudEliminacionServicio;
    this.ultimoProcesamiento = LocalDateTime.now();
  }

  @Scheduled(cron = "${cron.intervaloProcesamientoSolicitudes}")
  public void procesarSpam() {
    LocalDateTime nuevoValor = LocalDateTime.now(); // TODO: Ver si hace falta esto de guardar el valor o es extra
    solicitudEliminacionServicio.procesarSpam(ultimoProcesamiento);
    ultimoProcesamiento = nuevoValor;
  }
}
