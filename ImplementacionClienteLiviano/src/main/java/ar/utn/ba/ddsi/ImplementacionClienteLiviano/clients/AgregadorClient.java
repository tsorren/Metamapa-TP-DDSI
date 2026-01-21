package ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.internal.WebApiCallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.util.List;

@Slf4j
@Component
public class AgregadorClient {

  private final WebClient webClient;
  private final String agregadorUrl;
  //webApiCallerService automatiza el proceso de incluir el token en la solicitud y de refrescarlo de ser necesario
  private final WebApiCallerService webApiCallerService;

  public AgregadorClient(@Value("${agregador.url}") String agregadorUrl, WebApiCallerService webApiCallerService) {
    // Agregar protocolo HTTPS si no está presente
    String urlFinal = agregadorUrl;
    if (!agregadorUrl.startsWith("http://") && !agregadorUrl.startsWith("https://")) {
      urlFinal = "https://" + agregadorUrl;
      log.info("⚙️ Agregando protocolo HTTPS a la URL del agregador");
    }

    log.info("🔗 Configurando AgregadorClient con URL: {}", urlFinal);

    // Extraer host y puerto de la URL
    try {
      String[] urlParts = urlFinal.replace("https://", "").replace("http://", "").split("/")[0].split(":");
      String host = urlParts[0];
      String port = urlParts.length > 1 ? urlParts[1] : (urlFinal.startsWith("https://") ? "443" : "80");
      log.info("📍 Host: {}, Puerto: {}", host, port);
    } catch (Exception e) {
      log.warn("⚠️ No se pudo parsear la URL para mostrar host/puerto: {}", e.getMessage());
    }

    ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(32 * 1024 * 1024)) // 32 MB
            .build();
    this.webClient = WebClient.builder()
            .baseUrl(urlFinal)
            .exchangeStrategies(strategies)
            .build();
    this.agregadorUrl = urlFinal;
    this.webApiCallerService = webApiCallerService;

    log.info("✅ AgregadorClient inicializado correctamente");
  }

  public List<ColeccionDTO> obtenerColecciones() {
    // Endpoint público - no requiere autenticación
    try {
      return webClient.get()
              .uri("/colecciones")
              .retrieve()
              .bodyToFlux(ColeccionDTO.class)
              .collectList()
              .block();
    } catch (Exception e) {
      log.error("Error al obtener colecciones: {}", e.getMessage());
      return List.of();
    }
  }

  public ConsultarColeccionDTO obtenerColeccionDestacada() {
    // Endpoint público - no requiere autenticación
    try {
      return webClient.get()
              .uri("/colecciones/destacada")
              .retrieve()
              .bodyToMono(ConsultarColeccionDTO.class)
              .block();
    } catch (Exception e) {
      log.error("Error al obtener colección destacada: {}", e.getMessage());
      return null;
    }
  }

  public ConsultarColeccionDTO obtenerColeccionDestacada(Integer pagina, Integer tamanoPagina) {
    // Endpoint público - no requiere autenticación
    try {
      return webClient.get()
              .uri(uriBuilder -> uriBuilder
                      .path("/colecciones/destacada")
                      .queryParam("pagina", pagina != null ? pagina : 0)
                      .queryParam("tamano_pagina", tamanoPagina != null ? tamanoPagina : 10)
                      .build())
              .retrieve()
              .bodyToMono(ConsultarColeccionDTO.class)
              .block();
    } catch (Exception e) {
      log.error("Error al obtener colección destacada con paginación: {}", e.getMessage());
      return null;
    }
  }

  public ColeccionDTO obtenerColeccionPorHandle(String handle) {
    // Endpoint público - no requiere autenticación
    try {
      return webClient.get()
              .uri("/colecciones/{handle}", handle)
              .retrieve()
              .bodyToMono(ColeccionDTO.class)
              .block();
    } catch (Exception e) {
      log.error("Error al obtener colección por handle {}: {}", handle, e.getMessage());
      return null;
    }
  }

  public HechoDTO obtenerHechoPorId(Long id) {
    // Endpoint público - no requiere autenticación
    try {
      return webClient.get()
              .uri("/hechos/{id}", id)
              .retrieve()
              .bodyToMono(HechoDTO.class)
              .block();
    } catch (Exception e) {
      log.error("Error al obtener hecho por id {}: {}", id, e.getMessage());
      return null;
    }
  }

  public ConsultarColeccionDTO consultarColeccion(String handle, InformacionFiltrado filtrado) {
    // Endpoint público - no requiere autenticación
    return webClient.get()
        .uri(uriBuilder -> {
          UriBuilder builder = uriBuilder.path("/colecciones/{handle}");
          if (filtrado != null) {
            if (filtrado.getCategoria() != null) {
              builder.queryParam("categoria", filtrado.getCategoria());
            }
            if (filtrado.getTipoOrigen() != null) {
              builder.queryParam("tipo_origen", filtrado.getTipoOrigen());
            }
            if (filtrado.getNavegacionCurada() != null) {
              builder.queryParam("navegacion_curada", filtrado.getNavegacionCurada());
            }
            if (filtrado.getFechaReporteDesde() != null) {
              builder.queryParam("fecha_reporte_desde", filtrado.getFechaReporteDesde());
            }
            if (filtrado.getFechaReporteHasta() != null) {
              builder.queryParam("fecha_reporte_hasta", filtrado.getFechaReporteHasta());
            }
            if (filtrado.getFechaAcontecimientoDesde() != null) {
              builder.queryParam("fecha_acontecimiento_desde", filtrado.getFechaAcontecimientoDesde());
            }
            if (filtrado.getFechaAcontecimientoHasta() != null) {
              builder.queryParam("fecha_acontecimiento_hasta", filtrado.getFechaAcontecimientoHasta());
            }
            if (filtrado.getLatitud() != null) {
              builder.queryParam("latitud", filtrado.getLatitud());
            }
            if (filtrado.getLongitud() != null) {
              builder.queryParam("longitud", filtrado.getLongitud());
            }
            if (filtrado.getRadio() != null) {
              builder.queryParam("radio", filtrado.getRadio());
            }
          }
          return builder.build(handle);
        })
        .retrieve()
        .bodyToMono(ConsultarColeccionDTO.class)
        .block();
  }

  public UsuarioDTO obtenerUsuarioPorNombre(String nombreUsuario) {
    /*return webClient.get()
            .uri("/usuarios/{nombreUsuario}", nombreUsuario)
            .retrieve()
            .bodyToMono(UsuarioDTO.class)
            .block();*/
    return webApiCallerService.get(agregadorUrl + "/usuarios/" + nombreUsuario, UsuarioDTO.class);
  }

  public ContribuyenteOutputDTO registrarContribuyente(ContribuyenteOutputDTO contribuyente) {
      // El registro no requiere autenticación, se usa webClient directamente
      return webClient.post()
          .uri("/contribuyentes")
          .bodyValue(contribuyente)
          .retrieve()
          .bodyToMono(ContribuyenteOutputDTO.class)
          .block();
  }

  public AdministradorOutputDTO registrarAdministrador(AdministradorOutputDTO admin) {
      // El registro de administrador requiere autenticación (solo un admin puede crear otro admin)
      return webApiCallerService.post(agregadorUrl + "/admin", admin, AdministradorOutputDTO.class);
  }

  public ColeccionDTO crearColeccion(ColeccionInputDTO datos) {
    /*return webClient.post()
            .uri("/admin/colecciones")
            .bodyValue(datos)
            .retrieve()
            .bodyToMono(ColeccionDTO.class)
            .block();*/
    return webApiCallerService.post(agregadorUrl + "/admin/colecciones", datos, ColeccionDTO.class);
  }

  public ColeccionDTO actualizarColeccion(String handle, ColeccionInputDTO datos) {
       /*return webClient.patch()
            .uri("/admin/colecciones/{handle}", handle)
            .header("Authorization", "Bearer " + accessToken)
            .bodyValue(datos)
            .retrieve()
            .bodyToMono(ColeccionDTO.class)
            .block();*/
    return webApiCallerService.patch(agregadorUrl + "/admin/colecciones/"+handle, datos, ColeccionDTO.class);
  }

  public void eliminarColeccion(String handle) {
    /*webClient.delete()
            .uri("/admin/colecciones/{handle}", handle)
            .retrieve()
            .bodyToMono(Void.class)
            .block();*/
    webApiCallerService.delete(agregadorUrl + "/admin/colecciones/" + handle);
  }

  /**
   * Obtener solicitudes de eliminación pendientes
   */
  public List<SolicitudDeEliminacionOutputDTO> obtenerSolicitudesEliminacionPorEstado(EstadoAtencionSolicitud estado) {
    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/admin/solicitudes-eliminacion")
              .queryParam("estado", estado.name())
              .build())
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToFlux(SolicitudDeEliminacionOutputDTO.class)
          .collectList()
          .block();
    });
  }

  /**
   * Procesar solicitud de eliminación
   */
  public SolicitudDeEliminacionOutputDTO procesarSolicitudEliminacion(Long idSolicitud, ProcesamientoSolicitudEliminacionInputDTO procesamiento) {
      /*return webClient.patch()
          .uri("/admin/solicitudes-eliminacion/{idSolicitud}", idSolicitud)
          .header("Authorization", "Bearer " + accessToken)
          .bodyValue(procesamiento)
          .retrieve()
          .bodyToMono(SolicitudDeEliminacionOutputDTO.class)
          .block();*/
    return webApiCallerService.patch(agregadorUrl + "/admin/solicitudes-eliminacion/" + idSolicitud, procesamiento, SolicitudDeEliminacionOutputDTO.class);
  }

  /**
   * Solicitar eliminación de un hecho
   * Este endpoint es público - no requiere autenticación
   */
  public SolicitudDeEliminacionOutputDTO solicitarEliminacion(SolicitudEliminacionInputDTO solicitud) {
    /*return webClient.post()
            .uri("/solicitudes-eliminacion")
            .bodyValue(solicitud)
            .retrieve()
            .bodyToMono(SolicitudDeEliminacionOutputDTO.class)
            .block();*/
    return webApiCallerService.postPublic(agregadorUrl+"/solicitudes-eliminacion", solicitud, SolicitudDeEliminacionOutputDTO.class);
  }

  /**
   * Obtener hecho de fuente dinámica por ID externo
   * Este endpoint retorna el hecho con su ID interno de Agregador
   */
  public HechoOutputDTO obtenerHechoDeFuenteDinamica(Long idExterno) {
    /*return webClient.get()
            .uri("/admin/fuentes/dinamica/{id}", idExterno)
            .retrieve()
            .bodyToMono(HechoOutputDTO.class)
            .block();*/
    return webApiCallerService.get(agregadorUrl+"/admin/fuentes/dinamica/" + idExterno, HechoOutputDTO.class);
  }

  /**
   * Crear un criterio de pertenencia vacío
   */
  public CriterioDePertenenciaOutputDTO crearCriterioDePertenencia() {
    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.post()
          .uri("/admin/criteriosDePertenencia")
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(CriterioDePertenenciaOutputDTO.class)
          .block();
    });
  }

  /**
   * Crear un filtro
   */
  public FiltroOutputDTO crearFiltro(FiltroInputDTO filtro) {
    /*return webClient.post()
            .uri("/admin/filtros")
            .bodyValue(filtro)
            .retrieve()
            .bodyToMono(FiltroOutputDTO.class)
            .block();*/
    return webApiCallerService.post(agregadorUrl + "/admin/filtros", filtro, FiltroOutputDTO.class);
  }

  /**
   * Agregar un filtro a un criterio de pertenencia
   */
  public CriterioDePertenenciaOutputDTO agregarFiltroACriterio(Long idCriterio, Long idFiltro) {
    CriterioDePertenenciaInputDTO input = new CriterioDePertenenciaInputDTO(idFiltro, Accion.AGREGAR);
    /*return webClient.patch()
            .uri("/admin/criteriosDePertenencia/{id}", idCriterio)
            .bodyValue(input)
            .retrieve()
            .bodyToMono(CriterioDePertenenciaOutputDTO.class)
            .block();*/
    return webApiCallerService.patch(agregadorUrl+"/admin/criteriosDePertenencia/" + idCriterio, input, CriterioDePertenenciaOutputDTO.class);
  }

  /**
   * Quitar un filtro de un criterio de pertenencia
   */
  public CriterioDePertenenciaOutputDTO quitarFiltroDeCriterio(Long idCriterio, Long idFiltro) {
    CriterioDePertenenciaInputDTO input = new CriterioDePertenenciaInputDTO(idFiltro, Accion.QUITAR);
    return webApiCallerService.patch(agregadorUrl+"/admin/criteriosDePertenencia/" + idCriterio, input, CriterioDePertenenciaOutputDTO.class);
  }

  /**
   * Obtener criterio de pertenencia por ID
   */
  public CriterioDePertenenciaOutputDTO obtenerCriterioDePertenencia(Long idCriterio) {
    return webApiCallerService.get(agregadorUrl + "/admin/criteriosDePertenencia/" + idCriterio, CriterioDePertenenciaOutputDTO.class);
  }

  /**
   * Obtener estadísticas del agregador
   */
  public java.util.Map<String, Object> obtenerEstadisticas() {
    return webApiCallerService.executeWithTokenRetry(accessToken -> {
      return webClient.get()
          .uri("/admin/estadisticas")
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {})
          .block();
    });
  }

  /**
   * Verificar estado de salud del microservicio
   */
  public boolean verificarEstado() {
    try {
      webClient.get()
              .uri("/actuator/health")
              .retrieve()
              .bodyToMono(String.class)
              .block();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Levantar módulo de fuente estática en el agregador
   */
  public ModuloFuenteOutputDTO levantarFuente(String url, String tipo) {
    ModuloFuenteInputDTO input = new ModuloFuenteInputDTO(url, tipo);
    try {
      /*return webClient.post()
          .uri("/admin/modulo")
          .bodyValue(input)
          .retrieve()
          .bodyToMono(ModuloFuenteOutputDTO.class)
          .block();*/
      return webApiCallerService.post(agregadorUrl+"/admin/modulo", input, ModuloFuenteOutputDTO.class);
    } catch (Exception e) {
      // Extraer mensaje de error más específico si está disponible
      String errorMessage = e.getMessage();
      if (errorMessage != null && errorMessage.contains("503") || errorMessage.contains("SERVICE_UNAVAILABLE")) {
        throw new RuntimeException("El servicio de " + tipo + " no está disponible en " + url + ". Por favor, levanta el servicio primero.");
      }
      throw new RuntimeException("Error al levantar fuente: " + errorMessage, e);
    }
  }

  /**
   * Obtener todos los módulos de fuentes registrados
   */
  public List<FuenteOutputDTO> obtenerFuentesRegistradas() {
    /*return webClient.get()
            .uri("/admin/fuentes")
            .retrieve()
            .bodyToFlux(FuenteOutputDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(agregadorUrl+"/admin/fuentes", FuenteOutputDTO.class);
  }

  /**
   * Obtener todos los módulos registrados (sin necesidad de tener fuentes)
   */
  public List<ModuloFuenteOutputDTO> obtenerModulosRegistrados() {
    /*return webClient.get()
            .uri("/admin/modulo")
            .retrieve()
            .bodyToFlux(ModuloFuenteOutputDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(agregadorUrl+"/admin/modulo", ModuloFuenteOutputDTO.class);
  }

  // ==================== NOTIFICACIONES ====================

  public List<NotificacionDTO> obtenerNotificacionesDeAdministrador(Long idAdministrador) {
    /*return webClient.get()
            .uri("/notificaciones/administrador/{id}", idAdministrador)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(agregadorUrl + "/notificaciones/administrador/" + idAdministrador, NotificacionDTO.class);
  }

  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeAdministrador(Long idAdministrador) {
    /*return webClient.get()
            .uri("/notificaciones/administrador/{id}/no-leidas", idAdministrador)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(agregadorUrl+"/notificaciones/administrador/"+idAdministrador + "/no-leidas", NotificacionDTO.class);
  }

  public List<NotificacionDTO> obtenerNotificacionesDeContribuyente(Long idContribuyente) {
    /*return webClient.get()
            .uri("/notificaciones/contribuyente/{id}", idContribuyente)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return  webApiCallerService.getList(agregadorUrl + "/notificaciones/contribuyente/" + idContribuyente, NotificacionDTO.class);
  }

  public List<NotificacionDTO> obtenerNotificacionesNoLeidasDeContribuyente(Long idContribuyente) {
    /*return webClient.get()
            .uri("/notificaciones/contribuyente/{id}/no-leidas", idContribuyente)
            .retrieve()
            .bodyToFlux(NotificacionDTO.class)
            .collectList()
            .block();*/
    return webApiCallerService.getList(agregadorUrl+"/notificaciones/contribuyente/"+idContribuyente+"/no-leidas", NotificacionDTO.class);
  }

  public void marcarNotificacionComoLeida(Long idNotificacion) {
    webApiCallerService.executeWithTokenRetry(accessToken -> {
      return  webClient.patch()
          .uri("/notificaciones/{id}/marcar-leida", idNotificacion)
          .header("Authorization", "Bearer " + accessToken)
          .retrieve()
          .bodyToMono(Void.class)
          .block();
    });
  }
}
