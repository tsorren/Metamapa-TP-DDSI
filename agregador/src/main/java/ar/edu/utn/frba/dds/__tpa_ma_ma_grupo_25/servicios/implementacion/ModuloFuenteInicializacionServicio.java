package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IModuloFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Order(1) // Ejecutar ANTES que ColeccionInicializacionServicio
public class ModuloFuenteInicializacionServicio {

  private final IModuloFuenteServicio moduloFuenteServicio;
  private final IModuloFuenteRepositorioBD moduloFuenteRepositorio;

  @Value("${fuente.dinamica.url:http://localhost:8081}")
  private String fuenteDinamicaUrl;

  @Value("${fuente.estatica.url:http://localhost:8082}")
  private String fuenteEstaticaUrl;

  @Value("${fuente.proxy.url}")
  private String fuenteProxyUrl;

  public ModuloFuenteInicializacionServicio(
          IModuloFuenteServicio moduloFuenteServicio,
          IModuloFuenteRepositorioBD moduloFuenteRepositorio
  ) {
    this.moduloFuenteServicio = moduloFuenteServicio;
    this.moduloFuenteRepositorio = moduloFuenteRepositorio;
  }

  @PostConstruct
  public void inicializarModulosFuentes() {
    log.info("📋 Inicializando módulos de fuentes...");

    try {
      // Verificar cuántos módulos ya existen
      long cantidadModulos = moduloFuenteRepositorio.count();
      log.info("📊 Módulos existentes: {}", cantidadModulos);

      // Verificar si ya existe módulo de Fuente Dinámica
      boolean existeDinamica = moduloFuenteRepositorio.findAll().stream()
              .anyMatch(m -> m.getTipo() == TipoFuente.DINAMICA);

      if (!existeDinamica) {
        try {
          log.info("Registrando Fuente Dinámica en: {}", fuenteDinamicaUrl);
          moduloFuenteServicio.crear(TipoFuente.DINAMICA, fuenteDinamicaUrl);
          log.info("Fuente Dinámica registrada exitosamente");
        } catch (Exception e) {
          log.warn("No se pudo registrar Fuente Dinámica: {}", e.getMessage());
          log.info("Asegúrate de que Fuente Dinámica esté corriendo en {}", fuenteDinamicaUrl);
        }
      } else {
        log.info("✅ Fuente Dinámica ya está registrada");
      }

      // Verificar si ya existe módulo de Fuente Estática
      boolean existeEstatica = moduloFuenteRepositorio.findAll().stream()
              .anyMatch(m -> m.getTipo() == TipoFuente.ESTATICA);

      if (!existeEstatica) {
        try {
          log.info("Registrando Fuente Estática en: {}", fuenteEstaticaUrl);
          moduloFuenteServicio.crear(TipoFuente.ESTATICA, fuenteEstaticaUrl);
          log.info("Fuente Estática registrada exitosamente");
        } catch (Exception e) {
          log.warn("No se pudo registrar Fuente Estática: {}", e.getMessage());
          log.info("Asegúrate de que Fuente Estática esté corriendo en {}", fuenteEstaticaUrl);
        }
      } else {
        log.info("Fuente Estática ya está registrada");
      }

      boolean existeProxy = moduloFuenteRepositorio.findAll().stream()
              .anyMatch(m -> m.getTipo() == TipoFuente.PROXY);

      if (!existeProxy) {
        try {
          log.info("Registrando Fuente Proxy en: {}", fuenteProxyUrl);
          moduloFuenteServicio.crear(TipoFuente.PROXY, fuenteProxyUrl);
          log.info("Fuente Proxy registrada exitosamente");
        } catch (Exception e) {
          log.warn("No se pudo registrar Fuente Proxy: {}", e.getMessage());
          log.info("Asegúrate de que Fuente Proxy esté corriendo en {}", fuenteProxyUrl);
        }
      } else {
        log.info("Fuente Proxy ya está registrada");
      }

      // Verificar total final
      long modulosFinales = moduloFuenteRepositorio.count();
      log.info("Total de módulos de fuentes: {}", modulosFinales);

      if (modulosFinales == 0) {
        log.error("No hay fuentes registradas. La colección estará vacía.");
        log.info("Inicia las fuentes antes del Agregador");
      }

    } catch (Exception e) {
      log.error("Error al inicializar módulos de fuentes: {}", e.getMessage(), e);
    }
  }
}

