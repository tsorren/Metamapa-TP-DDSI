package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ConsultaColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IColeccionServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/colecciones")
public class PublicColeccionControlador {

  private final IColeccionServicio coleccionServicio;

  public PublicColeccionControlador(IColeccionServicio coleccionServicio) {
    this.coleccionServicio = coleccionServicio;
  }

  @GetMapping("")
  public List<ColeccionOutputDTO> consultarColecciones() {
    return coleccionServicio.encontrarTodas();
  }

  @GetMapping("/destacada")
  public ConsultaColeccionOutputDTO obtenerColeccionDestacada() {

    // Obtener la primera colección disponible como destacada
    List<ColeccionOutputDTO> colecciones = coleccionServicio.encontrarTodas();

    if (colecciones.isEmpty()) {
      log.warn("No hay colecciones disponibles para mostrar como destacada");
      // Devolver respuesta vacía en lugar de lanzar excepción
      return new ConsultaColeccionOutputDTO(null, List.of());
    }

    // Usar el handle de la primera colección
    ColeccionOutputDTO coleccion = colecciones.stream()
            .filter(c -> "Hechos en Bariloche".equals(c.getTitulo()))
            .findFirst()
            .orElse(colecciones.get(0));
    String handle = coleccion.getHandle();


    // SIMPLIFICADO: Filtrar SOLO por ubicación (Bariloche)
    // Coordenadas de Bariloche: -41.1335, -71.3103
    // Radio: 150 km para capturar toda la región
    NavegacionInputDTO filtrosDestacada = new NavegacionInputDTO(
            handle,
            false,
            new HechosFiltrandoInputDTO(
                    new Categoria(null),  // SIN filtro de categoría - traer todas
                    null,  // Cualquier origen
                    null,  // Sin filtro de fecha de reporte
                    null,
                    null,  // Sin filtro de fecha de acontecimiento
                    null,
                    new Ubicacion(-41.1335, -71.3103),  // Centro de Bariloche
                    150.0  // Radio de 150 km (amplio para capturar toda la región)
            )
    );

    ConsultaColeccionOutputDTO resultado = coleccionServicio.consultarColeccion(filtrosDestacada);
    log.debug("Hechos en Bariloche: {}", resultado.getHechos().size());

    if (resultado.getHechos().isEmpty()) {
      log.warn("No hay hechos en la coleccion destacada");
    }

    return resultado;
  }

  @GetMapping("/{handleColeccion}")
  public ConsultaColeccionOutputDTO consultarColeccion(
          @PathVariable("handleColeccion") String handle,
          @RequestParam(value = "navegacion_curada", required = false) Boolean navegacionCurada,
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
        log.warn("Tipo de origen invalido: {}", tipoOrigenStr);
      }
    }

    NavegacionInputDTO inputDTO = new NavegacionInputDTO(
            handle,
            navegacionCurada,
            new HechosFiltrandoInputDTO(
                    new Categoria(categoria),
                    tipoOrigen,
                    fechaReporteDesde,
                    fechaReporteHasta,
                    fechaAcontecimientoDesde,
                    fechaAcontecimientoHasta,
                    new Ubicacion(latitud, longitud),
                    radio
            )
    );
    log.debug("Consultando coleccion '{}'", handle);
    return coleccionServicio.consultarColeccion(inputDTO);
  }
}
