package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/hechos")
public class PublicHechoControlador {
  private final IHechoServicio hechoServicio;
  private final IFuenteServicio fuenteServicio;

  public PublicHechoControlador(IHechoServicio hechoServicio, IFuenteServicio fuenteServicio) {
    this.hechoServicio = hechoServicio;
    this.fuenteServicio = fuenteServicio;
  }

  @GetMapping("/diagnostico")
  public Map<String, Object> diagnosticoHechos() {
    Map<String, Object> diagnostico = new HashMap<>();

    log.info("=== DIAGNÓSTICO DE HECHOS ===");

    // 1. Obtener todas las fuentes
    List<Fuente> fuentes = fuenteServicio.encontrarTodas();
    diagnostico.put("totalFuentes", fuentes.size());
    log.info("📊 Total fuentes: {}", fuentes.size());

    // 2. Por cada fuente, obtener sus hechos
    for (Fuente fuente : fuentes) {
      log.info("🔍 Consultando fuente ID: {} | Módulo: {}",
              fuente.getId(),
              fuente.getModulo().getTipo());
      try {
        List<Hecho> hechosDeFuente = fuenteServicio.hechosDeFuente(fuente.getId());
        log.info("   📍 Hechos obtenidos: {}", hechosDeFuente.size());
        diagnostico.put("fuente_" + fuente.getId() + "_hechos", hechosDeFuente.size());
        diagnostico.put("fuente_" + fuente.getId() + "_tipo", fuente.getModulo().getTipo().toString());

        // Mostrar primeros 3 hechos
        hechosDeFuente.stream().limit(3).forEach(h ->
          log.info("      • {} | Categoría: {} | Ubicación: ({}, {})",
                  h.getTitulo(),
                  h.getCategoria() != null ? h.getCategoria().getNombre() : "null",
                  h.getUbicacion() != null ? h.getUbicacion().getLatitud() : "null",
                  h.getUbicacion() != null ? h.getUbicacion().getLongitud() : "null")
        );
      } catch (Exception e) {
        log.error("   ❌ Error al obtener hechos de fuente {}: {}", fuente.getId(), e.getMessage());
        diagnostico.put("fuente_" + fuente.getId() + "_error", e.getMessage());
      }
    }

    log.info("=== FIN DIAGNÓSTICO ===");
    return diagnostico;
  }

  @GetMapping()
  public List<HechoOutputDTO> encontrarTodos(
          @RequestParam(value = "categoria", required = false) String categoria,
          @RequestParam(value = "tipo_origen", required = false) String tipoOrigenStr,
          @RequestParam(value = "fecha_reporte_desde", required = false) LocalDateTime fechaReporteDesde,
          @RequestParam(value = "fecha_reporte_hasta", required = false) LocalDateTime fechaReporteHasta,
          @RequestParam(value = "fecha_acontecimiento_desde", required = false) LocalDate fechaAcontecimientoDesde,
          @RequestParam(value = "fecha_acontecimiento_hasta", required = false) LocalDate fechaAcontecimientoHasta,
          @RequestParam(value = "latitud", required = false) Double latitud,
          @RequestParam(value = "longitud", required = false) Double longitud,
          @RequestParam(value = "radio", required = false) Double radio
  ) {
    TipoOrigen tipoOrigen = null;
    if (tipoOrigenStr != null && !tipoOrigenStr.isEmpty()) {
      try {
        tipoOrigen = TipoOrigen.valueOf(tipoOrigenStr);
      } catch (IllegalArgumentException e) {
        log.warn("Tipo de origen inválido: {}", tipoOrigenStr);
      }
    }

    HechosFiltrandoInputDTO inputDTO = new HechosFiltrandoInputDTO(
            new Categoria(categoria),
            tipoOrigen,
            fechaReporteDesde,
            fechaReporteHasta,
            fechaAcontecimientoDesde,
            fechaAcontecimientoHasta,
            new Ubicacion(latitud, longitud),
            radio
    );
    return hechoServicio.encontrarTodos(inputDTO);
  }

  @GetMapping("/{id}")
  public HechoOutputDTO encontrarPorId(@PathVariable("id") long id) {
    return hechoServicio.encontrarPorId(id);
  }

  // TODO: Hacer verificacion de Administrador
  @PostMapping("/etiquetar")
  public HechoOutputDTO etiquetar(@RequestParam("idHecho") Long idHecho, @RequestParam("idEtiqueta") Long idEtiqueta) {
    return hechoServicio.etiquetar(idHecho, idEtiqueta);

  }
}
