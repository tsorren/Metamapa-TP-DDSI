package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EstadisticaService {

  private final WebClient webClient;
  private final GeocodingService geocodingService;

  private final ProvinciaEstadisticaRepositorioBD provinciaRepo;
  private final CategoriaEstadisticaRepositorioBD categoriaRepo;
  private final ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo;
  private final HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo;
  private final SolicitudSpamEstadisticaRepositorioBD spamRepo;
  private final HechoProcesadoRepositorioBD hechoProcesadoRepo;

  private final ObjectMapper mapper = new ObjectMapper();

  public EstadisticaService(
          WebClient.Builder webClientBuilder,
          GeocodingService geocodingService,
          ProvinciaEstadisticaRepositorioBD provinciaRepo,
          CategoriaEstadisticaRepositorioBD categoriaRepo,
          ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo,
          HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo,
          SolicitudSpamEstadisticaRepositorioBD spamRepo,
          HechoProcesadoRepositorioBD hechoProcesadoRepo) {

    this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
    this.geocodingService = geocodingService;

    this.provinciaRepo = provinciaRepo;
    this.categoriaRepo = categoriaRepo;
    this.provinciaCategoriaRepo = provinciaCategoriaRepo;
    this.horaCategoriaRepo = horaCategoriaRepo;
    this.spamRepo = spamRepo;
    this.hechoProcesadoRepo = hechoProcesadoRepo;
  }

  public void recalcularEstadisticas() {
    List<HechoInputDTO> hechos = obtenerHechosDelAgregador();

    // Procesar hechos → convertir a HechoProcesado
    List<HechoProcesado> hechosProcesados = hechos.stream()
            .map(dto -> HechoProcesado.desdeDTO(dto, geocodingService))
            .collect(Collectors.toList());
    hechoProcesadoRepo.saveAll(hechosProcesados);

    // 1) Provincia con mayor cantidad de hechos
    provinciaRepo.deleteAll();
    hechosProcesados.stream()
            .collect(Collectors.groupingBy(HechoProcesado::getProvincia, Collectors.counting()))
            .forEach((provincia, cantidad) -> {
              ProvinciaEstadistica e = new ProvinciaEstadistica();
              e.setProvincia(provincia);
              e.setCantidadHechos(cantidad);
              e.setFechaCalculo(LocalDateTime.now());
              provinciaRepo.save(e);
            });

    // 2) Categoría con mayor cantidad de hechos
    categoriaRepo.deleteAll();
    hechosProcesados.stream()
            .collect(Collectors.groupingBy(HechoProcesado::getCategoria, Collectors.counting()))
            .forEach((categoria, cantidad) -> {
              CategoriaEstadistica e = new CategoriaEstadistica();
              e.setCategoria(categoria);
              e.setCantidadHechos(cantidad);
              e.setFechaCalculo(LocalDateTime.now());
              categoriaRepo.save(e);
            });

    // 3) Provincia con mayor cantidad de hechos por categoría
    provinciaCategoriaRepo.deleteAll();
    hechosProcesados.stream()
            .collect(Collectors.groupingBy(HechoProcesado::getCategoria,
                    Collectors.groupingBy(HechoProcesado::getProvincia, Collectors.counting())))
            .forEach((categoria, mapaProv) -> mapaProv.forEach((provincia, cantidad) -> {
              ProvinciaCategoriaEstadistica e = new ProvinciaCategoriaEstadistica();
              e.setCategoria(categoria);
              e.setProvincia(provincia);
              e.setCantidadHechos(cantidad);
              e.setFechaCalculo(LocalDateTime.now());
              provinciaCategoriaRepo.save(e);
            }));

    // 4) Hora del día con más hechos por categoría
    horaCategoriaRepo.deleteAll();
    hechosProcesados.stream()
            .collect(Collectors.groupingBy(HechoProcesado::getCategoria,
                    Collectors.groupingBy(h -> h.getFechaHora().getHour(), Collectors.counting())))
            .forEach((categoria, mapaHoras) -> mapaHoras.forEach((hora, cantidad) -> {
              HoraCategoriaEstadistica e = new HoraCategoriaEstadistica();
              e.setCategoria(categoria);
              e.setHoraDelDia(hora);
              e.setCantidadHechos(cantidad);
              e.setFechaCalculo(LocalDateTime.now());
              horaCategoriaRepo.save(e);
            }));

    // 5) Cantidad de solicitudes de eliminación que son spam
    spamRepo.deleteAll();
    Long spamCount = obtenerSolicitudesSpamDelAgregador();
    SolicitudSpamEstadistica e = new SolicitudSpamEstadistica();
    e.setCantidadSpam(spamCount);
    e.setFechaCalculo(LocalDateTime.now());
    spamRepo.save(e);
  }


  private List<HechoInputDTO> obtenerHechosDelAgregador() {
    List<Map<String, Object>> response = webClient.get()
            .uri("/hechos")
            .retrieve()
            .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {
            })
            .collectList()
            .block();

    // Mapear los hechos a HechoInputDTO
    return mapper.convertValue(response, new TypeReference<List<HechoInputDTO>>() {
    });
  }

  private Long obtenerSolicitudesSpamDelAgregador() {
    return webClient.get()
            .uri("/solicitudes/spam")
            .retrieve()
            .bodyToMono(Long.class)
            .block();
  }
}
