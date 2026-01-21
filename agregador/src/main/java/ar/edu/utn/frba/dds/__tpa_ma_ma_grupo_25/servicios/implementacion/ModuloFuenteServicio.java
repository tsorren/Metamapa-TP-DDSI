package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ModuloFuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IModuloFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ModuloFuenteServicio implements IModuloFuenteServicio {
  private final IModuloFuenteRepositorioBD moduloFuenteRepositorio;
  private final IFuenteServicio fuenteServicio;
  private final WebClient.Builder webClientBuilder;

  public ModuloFuenteServicio(IModuloFuenteRepositorioBD moduloFuenteRepositorio, @Lazy IFuenteServicio fuenteServicio, WebClient.Builder webClientBuilder) {
    this.moduloFuenteRepositorio = moduloFuenteRepositorio;
    this.fuenteServicio = fuenteServicio;
    this.webClientBuilder = webClientBuilder;
  }

  @Override
  public ModuloFuenteOutputDTO crear(TipoFuente tipo, String url) {
    // Normalizar la URL: solo trim
    String urlNormalizada = url.trim();

    // Validar que el servicio esté disponible ANTES de abrir transacción (llamada HTTP fuera de transacción)
    WebClient tempClient = validarServicioDisponible(tipo, urlNormalizada);

    // Guardar el módulo (transacción automática del repo)
    ModuloFuente modulo = new ModuloFuente(tipo, urlNormalizada, tempClient);
    moduloFuenteRepositorio.save(modulo);
    log.info("<CREACION> Modulo Fuente con url: {}", urlNormalizada);

    // Cargar fuentes fuera de la transacción principal para evitar connection leaks
    this.cargarFuentes(modulo, null);

    return modulo.toDTO();
  }

  /**
   * Valida que el servicio esté disponible haciendo una llamada HTTP.
   * Esta operación se hace FUERA de una transacción para no mantener conexiones de BD abiertas.
   */
  private WebClient validarServicioDisponible(TipoFuente tipo, String urlNormalizada) {
    WebClient tempClient = webClientBuilder.baseUrl(urlNormalizada).build();

    try {
      log.info("Validando disponibilidad del servicio {} en {}", tipo, urlNormalizada);

      // Intentar hacer una petición GET al endpoint /fuentes para verificar disponibilidad
      String response = tempClient.get()
              .uri("/fuentes")
              .retrieve()
              .bodyToMono(String.class)
              .timeout(java.time.Duration.ofSeconds(10))
              .block();

      log.info("Servicio {} en {} está disponible. Respuesta: {}", tipo, urlNormalizada,
               response != null && response.length() > 100 ? response.substring(0, 100) + "..." : response);

      return tempClient;

    } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
      log.error("Error HTTP al validar servicio {} en {}: Status={}, Body={}", tipo, urlNormalizada, e.getStatusCode(), e.getResponseBodyAsString());
      throw new RuntimeException("No se puede crear el módulo. El servicio de " + tipo + " en " + urlNormalizada + " respondió con error HTTP " + e.getStatusCode() + ". Verifica la configuración del servicio.");
    } catch (org.springframework.web.reactive.function.client.WebClientRequestException e) {
      log.error("Error de conexión al validar servicio {} en {}: {}", tipo, urlNormalizada, e.getMessage());
      throw new RuntimeException("No se puede crear el módulo. No se puede conectar con el servicio de " + tipo + " en " + urlNormalizada + ". Verifica que el servicio esté levantado y la URL sea correcta.");
    } catch (Exception e) {
      // Detectar timeout por el mensaje de la excepción
      String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
      if (errorMsg.contains("timeout") || errorMsg.contains("timed out")) {
        log.error("Timeout al conectar con el servicio {} en {}: El servicio tardó más de 10 segundos en responder", tipo, urlNormalizada);
        throw new RuntimeException("No se puede crear el módulo. El servicio de " + tipo + " en " + urlNormalizada + " no responde (timeout). Por favor, asegúrate de que el servicio esté levantado y sea accesible.");
      }
      log.error("El servicio {} en {} NO está disponible o no responde. Error: {} - {}", tipo, urlNormalizada, e.getClass().getSimpleName(), e.getMessage());
      throw new RuntimeException("No se puede crear el módulo. El servicio de " + tipo + " no está disponible en " + urlNormalizada + ". Por favor, asegúrate de que el servicio esté levantado.");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ModuloFuenteOutputDTO> encontrarTodas() {
    return moduloFuenteRepositorio.findAll().stream().map(ModuloFuente::toDTO).toList();
  }

  @Override
  public void cargarFuentes(ModuloFuente modulo, LocalDateTime ultimoRefresco) {
    // Obtener datos del módulo (transacción automática del repo)

    log.debug("Consultando fuentes del modulo {} ({}): GET {}/fuentes?fechaAnteriorA={}",
            modulo.getId(), modulo.getTipo(), modulo.getBaseUrl(), ultimoRefresco);

    // Hacer llamada HTTP FUERA de transacción
    long startTime = System.currentTimeMillis();
    List<FuenteInputDTO> response;
    try {
      response = modulo.getApi().get()
              .uri(uriBuilder -> uriBuilder
                      .path("/fuentes")
                      .queryParam("fechaAnteriorA", ultimoRefresco)
                      .build())
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<List<FuenteInputDTO>>() {
              })
              .block();
    } catch (Exception e) {
      log.error("Error al cargar fuentes del modulo {}: {}", modulo.getId(), e.getMessage());
      throw new RuntimeException("Error al cargar fuentes del modulo: " + e.getMessage(), e);
    }

    long elapsed = System.currentTimeMillis() - startTime;

    if (response == null) {
      log.warn("Modulo {} devolvio respuesta null en {}ms", modulo.getTipo(), elapsed);
      return;
    }

    log.debug("Modulo {} devolvio {} fuentes en {}ms", modulo.getTipo(), response.size(), elapsed);

    if (response.isEmpty()) {
      log.debug("No hay fuentes nuevas desde {}", ultimoRefresco);
      return;
    }

    // Procesar fuentes de forma síncrona
    int fuentesCreadas = 0;
    int fuentesOmitidas = 0;

    for (FuenteInputDTO fuente : response) {
      try {
        FuenteOutputDTO resultado = fuenteServicio.crear(modulo, fuente);
        if (resultado != null) {
          fuentesCreadas++;
        } else {
          fuentesOmitidas++;
        }
      } catch (Exception e) {
        log.error("Error al crear fuente del módulo {}: {}", modulo.getTipo(), e.getMessage());
        fuentesOmitidas++;
      }
    }

    if (fuentesCreadas > 0) {
      log.info("<MODULO {}> Creadas {} fuentes nuevas", modulo.getTipo(), fuentesCreadas);
    }
    if (fuentesOmitidas > 0) {
      log.debug("<MODULO {}> Omitidas {} fuentes que ya existían", modulo.getTipo(), fuentesOmitidas);
    }
  }

  @Override
  public void pedirNuevasFuentes(LocalDateTime ultimoRefresco) {
    // Obtener módulos (transacción automática del repo)
    List<ModuloFuente> modulos = moduloFuenteRepositorio.findAll();
    log.debug("Procesando {} modulos de fuentes...", modulos.size());

    if (modulos.isEmpty()) {
      log.warn("No hay modulos de fuente registrados. Registra un modulo primero.");
      return;
    }

    // Procesar cada módulo de forma síncrona
    for (ModuloFuente m : modulos) {
      try {
        log.debug("Cargando fuentes del módulo {}", m.getId());
        this.cargarFuentes(m, ultimoRefresco);
      } catch (Exception e) {
        log.error("Error al procesar modulo {}: {}", m.getId(), e.getMessage());
      }
    }
    log.debug("Todos los módulos procesados");
  }

  @Override
  @Transactional(readOnly = true)
  public ModuloFuente findByTipoFuente(TipoFuente tipoFuente) {
    ModuloFuente modulo = moduloFuenteRepositorio.findByTipo(tipoFuente);
    if (modulo == null) {
      throw new RuntimeException("ModuloFuente no encontrado con tipo: " + tipoFuente);
    }
    return modulo;
  }
}
