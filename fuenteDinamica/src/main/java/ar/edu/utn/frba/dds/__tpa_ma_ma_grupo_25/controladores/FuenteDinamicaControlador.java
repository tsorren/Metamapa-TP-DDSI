package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteDinamicaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteDinamicaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/fuentes")
public class FuenteDinamicaControlador {

  private final IFuenteDinamicaServicio fuenteDinamicaServicio;

  public FuenteDinamicaControlador(IFuenteDinamicaServicio servicioFuenteEstatica) {
    this.fuenteDinamicaServicio = servicioFuenteEstatica;
  }

  @GetMapping()

  public List<FuenteDinamicaOutputDTO> consultarFuentes(
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {
    if (fechaAnteriorA != null) {
      return fuenteDinamicaServicio.encontrarTodas().stream().filter(f -> f.getFechaDeCreacion().isAfter(fechaAnteriorA)).collect(Collectors.toList());
    }
    return fuenteDinamicaServicio.encontrarTodas();
  }

  @GetMapping("/{id}")
  public List<HechoOutputDTO> hechosDeFuente(@PathVariable("id") Long id,
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {

    // Log comentado para producción
    // log.info("<REQUEST> Hechos de fuente {} con fechaAnteriorA: {}", id, fechaAnteriorA);

    if (fechaAnteriorA != null) {
      List<HechoOutputDTO> todosLosHechos = fuenteDinamicaServicio.hechosDeFuente(id);
      // log.info("<DEBUG> Total hechos visibles en fuente {}: {}", id, todosLosHechos.size());

      List<HechoOutputDTO> hechosFiltrados = todosLosHechos.stream()
              .filter(h -> {
                // Un hecho es "nuevo" si:
                // 1. Su fecha de carga es posterior a fechaAnteriorA, O
                // 2. Su fecha de última modificación existe Y es posterior a fechaAnteriorA
                boolean nuevoPorCarga = h.getFechaDeCarga() != null && h.getFechaDeCarga().isAfter(fechaAnteriorA);
                boolean nuevoPorModificacion = h.getFechaUltimaModificacion() != null && h.getFechaUltimaModificacion().isAfter(fechaAnteriorA);

                // Log de debug comentado para producción
                // if (nuevoPorCarga || nuevoPorModificacion) {
                //   log.debug("<DEBUG> Hecho '{}' incluido - FechaCarga: {}, FechaModif: {}, nuevoPorCarga: {}, nuevoPorModif: {}",
                //       h.getTitulo(), h.getFechaDeCarga(), h.getFechaUltimaModificacion(), nuevoPorCarga, nuevoPorModificacion);
                // }

                return nuevoPorCarga || nuevoPorModificacion;
              })
              .collect(Collectors.toList());

      // Log comentado para producción
      // log.info("<RESPONSE> Fuente {} devuelve {} hechos nuevos desde {}", id, hechosFiltrados.size(), fechaAnteriorA);
      return hechosFiltrados;
    }
    return fuenteDinamicaServicio.hechosDeFuente(id);
  }

  @GetMapping("/{idFuente}/{idHecho}")
  public HechoOutputDTO hechoPorId(@PathVariable("idFuente") Long idFuente, @PathVariable("idHecho") Long idHecho) {
    return fuenteDinamicaServicio.hechoPorId(idFuente, idHecho);
  }

  @PostMapping
  public HechoOutputDTO cargarHecho(@RequestBody HechoInputDTO input) {
    return fuenteDinamicaServicio.cargarHecho(input);
  }
  @PatchMapping("/{idHecho}")
  public HechoOutputDTO modificarHecho(@RequestBody HechoModificacionDTO input) {
    return fuenteDinamicaServicio.modificarHecho(input);
  }

  @GetMapping("/admin/estadisticas")
  public EstadisticasFuenteDinamicaDTO obtenerEstadisticas() {
    // Log comentado para producción
    // log.info("<REQUEST> Solicitando estadísticas de FuenteDinamica");
    return fuenteDinamicaServicio.obtenerEstadisticas();
  }
}
