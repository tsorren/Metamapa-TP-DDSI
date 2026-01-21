package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudDeCreacionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class solicitudesPlanificador {
  private final ISolicitudDeCreacionServicio solicitudDeCreacionServicio;

  public solicitudesPlanificador(ISolicitudDeCreacionServicio solicitudDeCreacionServicio) {
    this.solicitudDeCreacionServicio = solicitudDeCreacionServicio;
  }

  @Scheduled(cron = "${cron.intervaloChequeoPendientes}")
  public void procesarSolicitudesPendientes() {
    solicitudDeCreacionServicio.procesarPendientesDeModificacion();
    // Log comentado para producción - descomentar solo si se necesita debug
    // log.debug("Procesando solicitudes pendientes de modificación");
  }
}
