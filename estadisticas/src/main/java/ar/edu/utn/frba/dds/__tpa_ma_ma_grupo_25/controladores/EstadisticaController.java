package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.EstadisticaService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

  private final EstadisticaService estadisticaService;
  private final ProvinciaEstadisticaRepositorioBD provinciaRepo;
  private final CategoriaEstadisticaRepositorioBD categoriaRepo;
  private final ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo;
  private final HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo;
  private final SolicitudSpamEstadisticaRepositorioBD spamRepo;
  private final HechoProcesadoRepositorioBD hechoProcesadoRepo;

  public EstadisticaController(
          EstadisticaService estadisticaService,
          ProvinciaEstadisticaRepositorioBD provinciaRepo,
          CategoriaEstadisticaRepositorioBD categoriaRepo,
          ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo,
          HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo,
          SolicitudSpamEstadisticaRepositorioBD spamRepo,
          HechoProcesadoRepositorioBD hechoProcesadoRepo
  ) {
    this.estadisticaService = estadisticaService;
    this.provinciaRepo = provinciaRepo;
    this.categoriaRepo = categoriaRepo;
    this.provinciaCategoriaRepo = provinciaCategoriaRepo;
    this.horaCategoriaRepo = horaCategoriaRepo;
    this.spamRepo = spamRepo;
    this.hechoProcesadoRepo = hechoProcesadoRepo;
  }

  @GetMapping("/general")
  public Map<String, Object> obtenerEstadisticasGenerales() {
    Map<String, Object> estadisticas = new HashMap<>();

    try {
      // Total de hechos procesados
      List<HechoProcesado> hechosProcesados = hechoProcesadoRepo.findAll();
      estadisticas.put("totalHechosProcesados", hechosProcesados.size());

      // Total de solicitudes spam
      List<SolicitudSpamEstadistica> solicitudesSpam = spamRepo.findAll();
      estadisticas.put("totalSolicitudesSpam", solicitudesSpam.size());

      // Estadísticas por provincia
      List<ProvinciaEstadistica> provincias = provinciaRepo.findAll();
      estadisticas.put("totalProvincias", provincias.size());
      if (!provincias.isEmpty()) {
        ProvinciaEstadistica provinciaTop = provincias.stream()
                .max(Comparator.comparing(ProvinciaEstadistica::getCantidadHechos))
                .orElse(null);
        if (provinciaTop != null) {
          estadisticas.put("provinciaConMasHechos", provinciaTop.getProvincia());
          estadisticas.put("cantidadHechosProvinciaTop", provinciaTop.getCantidadHechos());
        }
      }

      // Estadísticas por categoría
      List<CategoriaEstadistica> categorias = categoriaRepo.findAll();
      estadisticas.put("totalCategorias", categorias.size());
      if (!categorias.isEmpty()) {
        CategoriaEstadistica categoriaTop = categorias.stream()
                .max(Comparator.comparing(CategoriaEstadistica::getCantidadHechos))
                .orElse(null);
        if (categoriaTop != null) {
          estadisticas.put("categoriaConMasHechos", categoriaTop.getCategoria());
          estadisticas.put("cantidadHechosCategoriaTop", categoriaTop.getCantidadHechos());
        }
      }

    } catch (Exception e) {
      estadisticas.put("error", e.getMessage());
    }

    return estadisticas;
  }

  @GetMapping("/hechos/procesados")
  public Map<String, Object> obtenerEstadisticasHechosProcesados() {
    Map<String, Object> estadisticas = new HashMap<>();
    List<HechoProcesado> hechos = hechoProcesadoRepo.findAll();
    estadisticas.put("totalHechosProcesados", hechos.size());
    estadisticas.put("hechos", hechos);
    return estadisticas;
  }

  @GetMapping("/solicitudes/spam")
  public Map<String, Object> obtenerEstadisticasSolicitudesSpam() {
    Map<String, Object> estadisticas = new HashMap<>();
    List<SolicitudSpamEstadistica> solicitudes = spamRepo.findAll();
    estadisticas.put("totalSolicitudesSpam", solicitudes.size());
    estadisticas.put("solicitudes", solicitudes);
    return estadisticas;
  }

  @GetMapping("/provincias")
  public Map<String, Object> obtenerEstadisticasProvincias() {
    Map<String, Object> estadisticas = new HashMap<>();
    List<ProvinciaEstadistica> provincias = provinciaRepo.findAll();
    estadisticas.put("totalProvincias", provincias.size());
    estadisticas.put("provincias", provincias);

    if (!provincias.isEmpty()) {
      ProvinciaEstadistica provinciaTop = provincias.stream()
              .max(Comparator.comparing(ProvinciaEstadistica::getCantidadHechos))
              .orElse(null);
      if (provinciaTop != null) {
        estadisticas.put("provinciaConMasHechos", provinciaTop.getProvincia());
        estadisticas.put("cantidadHechosProvinciaTop", provinciaTop.getCantidadHechos());
      }
    }

    return estadisticas;
  }

  @GetMapping("/categorias")
  public Map<String, Object> obtenerEstadisticasCategorias() {
    Map<String, Object> estadisticas = new HashMap<>();
    List<CategoriaEstadistica> categorias = categoriaRepo.findAll();
    estadisticas.put("totalCategorias", categorias.size());
    estadisticas.put("categorias", categorias);

    if (!categorias.isEmpty()) {
      CategoriaEstadistica categoriaTop = categorias.stream()
              .max(Comparator.comparing(CategoriaEstadistica::getCantidadHechos))
              .orElse(null);
      if (categoriaTop != null) {
        estadisticas.put("categoriaConMasHechos", categoriaTop.getCategoria());
        estadisticas.put("cantidadHechosCategoriaTop", categoriaTop.getCantidadHechos());
      }
    }

    return estadisticas;
  }

  @GetMapping("/horas")
  public Map<String, Object> obtenerEstadisticasHoras() {
    Map<String, Object> estadisticas = new HashMap<>();
    List<HoraCategoriaEstadistica> horas = horaCategoriaRepo.findAll();
    estadisticas.put("totalRegistros", horas.size());
    estadisticas.put("horasCategorias", horas);
    return estadisticas;
  }

  @PostMapping("/recalcular")
  public String recalcular() {
    estadisticaService.recalcularEstadisticas();
    return "Estadísticas recalculadas correctamente.";
  }

  @GetMapping("/provincia")
  public List<ProvinciaEstadistica> getEstadisticasProvincia() {
    return provinciaRepo.findAll();
  }

  @GetMapping("/categoria")
  public List<CategoriaEstadistica> getEstadisticasCategoria() {
    return categoriaRepo.findAll();
  }

  @GetMapping("/provincia-categoria")
  public List<ProvinciaCategoriaEstadistica> getEstadisticasProvinciaCategoria() {
    return provinciaCategoriaRepo.findAll();
  }

  @GetMapping("/hora-categoria")
  public List<HoraCategoriaEstadistica> getEstadisticasHoraCategoria() {
    return horaCategoriaRepo.findAll();
  }

  @GetMapping("/spam")
  public List<SolicitudSpamEstadistica> getEstadisticasSpam() {
    return spamRepo.findAll();
  }

  @GetMapping("/buscar")
  public List<HechoProcesado> busquedaLibre(@RequestParam String texto) {
    return hechoProcesadoRepo.busquedaLibre(texto);
  }
}
