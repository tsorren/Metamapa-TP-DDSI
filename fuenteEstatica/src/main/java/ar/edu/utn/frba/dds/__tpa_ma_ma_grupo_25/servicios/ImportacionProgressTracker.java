package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImportacionProgressTracker {
  private final Map<Long, ProgresoImportacion> progresos = new ConcurrentHashMap<>();

  public void iniciar(Long fuenteId, int totalHechos) {
    ProgresoImportacion progreso = new ProgresoImportacion();
    progreso.setFuenteId(fuenteId);
    progreso.setTotalHechos(totalHechos);
    progreso.setProcesados(0);
    progreso.setErrores(0);
    progreso.setEnProgreso(true);
    progreso.setPorcentaje(0);
    progresos.put(fuenteId, progreso);
  }

  public void actualizar(Long fuenteId, int procesados, int errores) {
    ProgresoImportacion progreso = progresos.get(fuenteId);
    if (progreso != null) {
      progreso.setProcesados(procesados);
      progreso.setErrores(errores);
      progreso.setPorcentaje((int) ((procesados * 100.0) / progreso.getTotalHechos()));
    }
  }

  public void finalizar(Long fuenteId, boolean exitoso, String mensaje) {
    ProgresoImportacion progreso = progresos.get(fuenteId);
    if (progreso != null) {
      progreso.setEnProgreso(false);
      progreso.setExitoso(exitoso);
      progreso.setMensaje(mensaje);
      progreso.setPorcentaje(100);
    }
  }

  public ProgresoImportacion obtenerProgreso(Long fuenteId) {
    return progresos.get(fuenteId);
  }

  public void limpiar(Long fuenteId) {
    progresos.remove(fuenteId);
  }

  @Data
  public static class ProgresoImportacion {
    private Long fuenteId;
    private int totalHechos;
    private int procesados;
    private int errores;
    private int porcentaje;
    private boolean enProgreso;
    private boolean exitoso;
    private String mensaje;
  }
}

