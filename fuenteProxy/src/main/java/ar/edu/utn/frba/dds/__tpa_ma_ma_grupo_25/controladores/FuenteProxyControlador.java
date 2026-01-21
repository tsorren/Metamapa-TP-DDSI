package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CrearFuenteProxyExternaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.CrearFuenteProxyRecursivaDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteProxyInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EstadisticasFuenteProxyDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteProxyOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteProxyServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteProxyServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping()
public class FuenteProxyControlador {
  IFuenteProxyServicio fuenteProxyService;

  public FuenteProxyControlador(FuenteProxyServicio fuenteProxyService) {
    this.fuenteProxyService = fuenteProxyService;
  }

  @PostMapping()
  public FuenteProxyOutputDTO crear(@RequestBody FuenteProxyInputDTO inputDTO) {
    return fuenteProxyService.crear(inputDTO.getUrl());
  }

  @PostMapping("/recursiva")
  public FuenteProxyOutputDTO crearFuenteRecursiva(@RequestBody CrearFuenteProxyRecursivaDTO inputDTO) {
    // Log comentado para producción - el servicio ya loguea la creación
    // log.info("<REQUEST> Creando fuente proxy recursiva con URL: {}", inputDTO.getUrl());
    return fuenteProxyService.crearFuenteRecursiva(inputDTO.getUrl(), inputDTO.getNombre());
  }

  @PostMapping("/externa")
  public FuenteProxyOutputDTO crearFuenteExterna(@RequestBody CrearFuenteProxyExternaDTO inputDTO) {
    // Log comentado para producción - el servicio ya loguea la creación
    // log.info("<REQUEST> Creando fuente proxy externa con URL: {}", inputDTO.getUrl());
    return fuenteProxyService.crearFuenteExterna(
            inputDTO.getUrl(),
            inputDTO.getEmail(),
            inputDTO.getPassword(),
            inputDTO.getNombre()
    );
  }

  @GetMapping("/fuentes")
  public List<FuenteProxyOutputDTO> encontrarFuentes(
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {
    if (fechaAnteriorA != null) {
      return fuenteProxyService.encontrarFuentes().stream().filter(f -> f.getFechaDeCreacion().isAfter(fechaAnteriorA)).toList();
    }
    return fuenteProxyService.encontrarFuentes();
  }

  @GetMapping("/fuentes/{id}")
  public List<HechoOutputDTO> encontrarHechosDeFuente(
      @PathVariable("id") Long identificador,
      @RequestParam(value = "fechaAnteriorA", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaAnteriorA) {
    return fuenteProxyService.encontrarHechos(identificador, fechaAnteriorA);
  }

  @GetMapping("/fuentes/{idFuente}/{idHecho}")
  public HechoOutputDTO encontrarHechoPorId(@PathVariable("idFuente") Long idFuente, @PathVariable("idHecho") Long idHecho) {
    return fuenteProxyService.encontrarHechoPorId(idFuente, idHecho);
  }

  @GetMapping("/admin/estadisticas")
  public EstadisticasFuenteProxyDTO obtenerEstadisticas() {
    // Log comentado para producción
    // log.info("<REQUEST> Solicitando estadísticas de FuenteProxy");
    return fuenteProxyService.obtenerEstadisticas();
  }

}
