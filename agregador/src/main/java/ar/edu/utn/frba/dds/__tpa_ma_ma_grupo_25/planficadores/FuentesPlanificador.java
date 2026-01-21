package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class FuentesPlanificador {
  private final IFuenteServicio fuenteServicio;
  private final IModuloFuenteServicio moduloFuenteServicio;
  private final AtomicReference<LocalDateTime> ultimoRefresco;

  public FuentesPlanificador(IFuenteServicio fuenteServicio, IModuloFuenteServicio moduloFuenteServicio) {
    this.fuenteServicio = fuenteServicio;
    this.moduloFuenteServicio = moduloFuenteServicio;
    this.ultimoRefresco = new AtomicReference<>(LocalDateTime.now());
    log.info("FuentesPlanificador inicializado");
  }

  // Ejecuta cada minuto (a los 0 segundos)
  @Scheduled(cron = "${cron.intervaloRefresco}")
  public void refrescarColecciones() {
    log.debug("Iniciando refresco de colecciones...");

    LocalDateTime ultimoRefrescoActual = ultimoRefresco.get();
    LocalDateTime nuevoValor = LocalDateTime.now();

    try {
      log.info("Pidiendo nuevas fuentes desde: {}", ultimoRefrescoActual);
      moduloFuenteServicio.pedirNuevasFuentes(ultimoRefrescoActual);

      log.info("Refrescando hechos de fuentes desde: {}", ultimoRefrescoActual);
      fuenteServicio.refrescarHechosDeFuentes(ultimoRefrescoActual);

      ultimoRefresco.set(nuevoValor);
      log.info("Refresco completado. Proximo refresco: {}", nuevoValor);
    } catch (Exception e) {
      log.error("Error al refrescar colecciones: {}", e.getMessage(), e);
    }
  }

  public LocalDateTime getUltimoRefresco() {
    return ultimoRefresco.get();
  }
}
