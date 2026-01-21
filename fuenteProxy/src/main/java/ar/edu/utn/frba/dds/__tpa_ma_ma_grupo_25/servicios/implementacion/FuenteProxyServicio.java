package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.metamapa.HechoMetamapaInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteProxyDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteProxyOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteDesastres;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteMetamapa;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes.FuenteProxy;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteProxyRepositorio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteProxyServicio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@DependsOn("fuenteProxyRepositorio")
public class FuenteProxyServicio implements IFuenteProxyServicio {
  private final IFuenteProxyRepositorio fuenteProxyRepositorio;
  private final WebClient.Builder webClientBuilder;

  @Value("${desastresApi.email}")
  private String desastresApiEmail;

  @Value("${desastresApi.password}")
  private String desastresApiPassword;

  @Value("${desastresApi.baseUrl}")
  private String desastresApiBaseUrl;

  public FuenteProxyServicio(
          IFuenteProxyRepositorio fuenteProxyRepositorio,
          WebClient.Builder webClientBuilder
  ) {
    this.fuenteProxyRepositorio = fuenteProxyRepositorio;
    this.webClientBuilder = webClientBuilder;
  }

  @PostConstruct
  public void inicializarFuenteDesastresPorDefecto() {
    // Verificar si ya existe una FuenteDesastres en la BD/caché
    if(fuenteProxyRepositorio.encontrarTodas().stream().noneMatch(f -> f instanceof FuenteDesastres)) {
      log.info("No se encontró fuente Desastres por defecto, creando una nueva...");
      FuenteDesastres fuente = new FuenteDesastres(
              webClientBuilder.baseUrl(desastresApiBaseUrl).build(),
              desastresApiBaseUrl,
              desastresApiEmail,
              desastresApiPassword
      );
      fuenteProxyRepositorio.guardar(fuente);
      log.info("Fuente Desastres por defecto creada con id={}", fuente.getIdentificador());
    } else {
      log.info("Ya existe una fuente Desastres en la base de datos");
    }
  }

  public static HechoOutputDTO hechoOutputDTO(Hecho h) {
    return new HechoOutputDTO(
            h.getIdentificador(),
            h.getTitulo(),
            h.getDescripcion(),
            h.getCategoria(),
            h.getUbicacion(),
            h.getFechaDelHecho(),
            h.getFechaDeCarga(),
            h.getContenido(),
            h.getOrigen(),
            h.getEtiquetas(),
            h.getVisible()
    );
  }

  public static Hecho hechoMetamapaInputDTO(HechoMetamapaInputDTO h) {
    return new Hecho(
            h.getIdentificador(),
            h.getTitulo(),
            h.getDescripcion(),
            h.getCategoria(),
            h.getUbicacion(),
            h.getFechaDelHecho(),
            h.getFechaDeCarga(),
            h.getContenido(),
            h.getOrigen(),
            h.getEtiquetas(),
            h.getVisible()
    );
  }

  public static FuenteProxyOutputDTO fuenteProxyOutputDTO(FuenteProxy f) {
    return new FuenteProxyOutputDTO(f.getIdentificador()
            , LocalDateTime.now()
    );
  }

  public FuenteProxyOutputDTO crear(String url) {
    FuenteProxy fuente = new FuenteMetamapa(webClientBuilder.baseUrl(url).build(), url);
    fuenteProxyRepositorio.guardar(fuente);
    log.info("Fuente proxy creada id={}", fuente.getIdentificador());
    return FuenteProxyServicio.fuenteProxyOutputDTO(fuente);
  }

  @Override
  public FuenteProxyOutputDTO crearFuenteRecursiva(String url, String nombre) {
    log.info("Creando fuente proxy Metamapa url={}", url);
    FuenteProxy fuente = new FuenteMetamapa(webClientBuilder.baseUrl(url).build(), url);
    fuenteProxyRepositorio.guardar(fuente);
    log.info("Fuente proxy Metamapa creada id={}", fuente.getIdentificador());
    return FuenteProxyServicio.fuenteProxyOutputDTO(fuente);
  }

  @Override
  public FuenteProxyOutputDTO crearFuenteExterna(String url, String email, String password, String nombre) {
    log.info("Creando fuente proxy externa url={}", url);
    FuenteProxy fuente = new FuenteDesastres(
            webClientBuilder.baseUrl(url).build(),
            url,
            email,
            password
    );
    fuenteProxyRepositorio.guardar(fuente);
    log.info("Fuente proxy externa creada id={}", fuente.getIdentificador());
    return FuenteProxyServicio.fuenteProxyOutputDTO(fuente);
  }

  @Override
  public List<FuenteProxyOutputDTO> encontrarFuentes() {
    List<FuenteProxy> fuentes = fuenteProxyRepositorio.encontrarTodas();
    // Log de consulta comentado para producción
    // log.info("<CONSULTA> {} Fuentes disponibles", fuentes.size());
    return fuentes.stream().map(FuenteProxyServicio::fuenteProxyOutputDTO).toList();
  }

  @Override
  public List<HechoOutputDTO> encontrarHechos(Long idFuente, LocalDateTime fechaAnteriorA) {
    FuenteProxy fuente = this.fuenteProxyRepositorio.encontrarPorId(idFuente);
    if (fuente == null) {
      log.warn("Fuente con id={} no encontrada o no está disponible", idFuente);
      return List.of();
    }

    List<Hecho> hechos = fuente.hechos();

    // Filtrar por fecha si se proporciona
    if (fechaAnteriorA != null) {
      hechos = hechos.stream()
          .filter(h -> h.getFechaDeCarga() != null && h.getFechaDeCarga().isAfter(fechaAnteriorA))
          .toList();
    }

    // Log de consulta comentado para producción
    // log.info("<CONSULTA> Fuente {} respondio con {} hechos", idFuente, hechos.size());
    return hechos.stream().map(FuenteProxyServicio::hechoOutputDTO).toList();
  }

  @Override
  public HechoOutputDTO encontrarHechoPorId(Long idFuente, Long idHecho) {
    FuenteProxy fuente = this.fuenteProxyRepositorio.encontrarPorId(idFuente);
    if (fuente == null) {
      log.warn("Fuente con id={} no encontrada o no está disponible", idFuente);
      return null;
    }
    Hecho hecho = fuente.hechoPorId(idHecho);
    // Log de consulta comentado para producción
    // log.info("<CONSULTA> Fuente {} respondio con hecho de id {}", idFuente, idHecho);
    return hechoOutputDTO(hecho);
  }

  @Override
  public EstadisticasFuenteProxyDTO obtenerEstadisticas() {
    List<FuenteProxy> fuentes = fuenteProxyRepositorio.encontrarTodas();

    int totalFuentes = fuentes.size();
    int totalHechos = 0;

    // Contar todos los hechos de todas las fuentes
    for (FuenteProxy fuente : fuentes) {
      try {
        List<Hecho> hechos = fuente.hechos();
        totalHechos += hechos.size();
      } catch (Exception e) {
        log.warn("Error al obtener hechos de fuente {}: {}", fuente.getIdentificador(), e.getMessage());
      }
    }

    // Log de estadísticas comentado para producción
    // log.info("<ESTADISTICAS> Fuentes creadas: {}, Hechos importados: {}", totalFuentes, totalHechos);
    return new EstadisticasFuenteProxyDTO(totalFuentes, totalHechos);
  }

}

