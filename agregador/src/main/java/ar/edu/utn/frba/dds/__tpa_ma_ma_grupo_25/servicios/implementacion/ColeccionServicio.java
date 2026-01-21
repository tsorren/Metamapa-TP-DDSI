package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ColeccionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ConsultaColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IColeccionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICriteriosPertenenciaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IColeccionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ColeccionServicio implements IColeccionServicio {
  private final IColeccionRepositorioBD coleccionRepositorio;
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final ICriteriosPertenenciaRepositorioBD criteriosPertenenciaRepositorio;
  private final IFuenteServicio fuenteServicio;

  public ColeccionServicio(
          IColeccionRepositorioBD coleccionRepositorio,
          IFuenteRepositorioBD fuenteRepositorio,
          ICriteriosPertenenciaRepositorioBD criteriosRepositorio, IFuenteServicio fuenteServicio
  ) {
    this.coleccionRepositorio = coleccionRepositorio;
    this.fuenteRepositorio = fuenteRepositorio;
    this.criteriosPertenenciaRepositorio = criteriosRepositorio;
    this.fuenteServicio = fuenteServicio;
  }

  @Override
  @Transactional
  public ColeccionOutputDTO crear(ColeccionInputDTO coleccionInputDto) {
    Coleccion nuevaColeccion = this.coleccionInputDTO(coleccionInputDto);
    this.coleccionRepositorio.save(nuevaColeccion);
    log.info("<CREACION> Coleccion con handle {}", nuevaColeccion.getHandle());
    return nuevaColeccion.toDTO();
  }

  private List<Hecho> hechosDeFuente(Fuente fuente) {
    return fuenteServicio.hechosDeFuente(fuente.getId());
  }

  @Transactional(readOnly = true)
  public List<Hecho> hechosDeColeccion(String handleColeccion) {
    log.debug("Obteniendo hechos para coleccion: {}", handleColeccion);

    Coleccion coleccion = coleccionRepositorio.findByHandle(handleColeccion);
    log.debug("Coleccion '{}' tiene {} fuentes configuradas",
        coleccion.getTitulo(), coleccion.getFuentes().size());

    List<Hecho> hechosBase = coleccion.getFuentes().stream()
            .flatMap(fuente -> {
              List<Hecho> hechosFuente = hechosDeFuente(fuente);
              log.debug("   - Fuente {}: {} hechos totales", fuente.getId(), hechosFuente.size());
              return hechosFuente.stream();
            })
            .filter(h -> {
              boolean visible = h.getVisible();
              if (!visible) {
                log.debug("   Hecho {} filtrado por visible=false", h.getId());
              }
              return visible;
            })
            .toList();

    log.debug("Total hechos visibles antes de criterio: {}", hechosBase.size());

    List<Hecho> hechosFinales = coleccion.getCriterioDePertenencia().aplicar(hechosBase);
    log.debug("Total hechos despues de criterio de pertenencia: {}", hechosFinales.size());

    return hechosFinales;
  }

  @Override
  @Transactional(readOnly = true)
  public ConsultaColeccionOutputDTO consultarColeccion(NavegacionInputDTO i) {
    log.debug("Consultando coleccion - Handle: {}", i.getHandle());

    Coleccion coleccion = coleccionRepositorio.findByHandle(i.getHandle());
    log.debug("Coleccion encontrada: '{}' con {} fuentes",
        coleccion.getTitulo(), coleccion.getFuentes().size());

    // Obtener hechos base de la colección
    List<Hecho> hechosBase = this.hechosDeColeccion(coleccion.getHandle());
    log.debug("Hechos base de coleccion (antes de filtros adicionales): {}", hechosBase.size());

    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterio.generarFiltros(i.getFiltros(), i.getNavegacionCurada(), coleccion.getAlgoritmoConsenso());
    log.debug("Filtros generados: {}", criterio.getFiltros().size());

    // Aplicar filtros adicionales
    List<Hecho> hechosFiltrados = criterio.aplicar(hechosBase);
    log.debug("Hechos despues de aplicar filtros: {}", hechosFiltrados.size());

    log.info("Consulta coleccion {} respondio con {} hechos",
        i.getHandle(), hechosFiltrados.size());

    return new ConsultaColeccionOutputDTO(
            coleccion.toDTO(),
            hechosFiltrados.stream().map(Hecho::toDTO).collect(Collectors.toList())
    );
  }

  @Override
  @Transactional(readOnly = true)
  public List<ColeccionOutputDTO> encontrarTodas() {
    return coleccionRepositorio.findAll().stream().map(Coleccion::toDTO).toList();
  }


  public Coleccion coleccionInputDTO(ColeccionInputDTO coleccionDto) {
    CriterioDePertenencia criterioDePertenencia = criteriosPertenenciaRepositorio.findById(coleccionDto.getCriterioDePertenenciaID()).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: "));
    List<Fuente> fuentes = coleccionDto.getIdsFuentes().stream()
            .map(fuenteRepositorio::findById)
            .flatMap(Optional::stream)
            .toList();
    return Coleccion.crear(coleccionDto.getTitulo(), coleccionDto.getDescripcion(), criterioDePertenencia, fuentes, coleccionDto.getAlgoritmoConsenso());
  }


  @Override
  @Transactional
  public void eliminar(String handle) {
    Coleccion coleccion = this.coleccionRepositorio.findByHandle(handle);
    this.coleccionRepositorio.delete(coleccion);
    log.info("<ELIMINACION> Coleccion con handle {}", coleccion.getHandle());
  }

  @Override
  @Transactional
  public ColeccionOutputDTO modificar(String handle, ColeccionInputDTO coleccionInput) {
    Coleccion coleccionAModificar = this.coleccionRepositorio.findByHandle(handle);
    if (coleccionInput.getTitulo() != null) {
      coleccionAModificar.setTitulo(coleccionInput.getTitulo());
    }
    if (coleccionInput.getDescripcion() != null) {
      coleccionAModificar.setDescripcion(coleccionInput.getDescripcion());
    }
    if (coleccionInput.getCriterioDePertenenciaID() != null) {
      CriterioDePertenencia criterio = criteriosPertenenciaRepositorio.findById(coleccionInput.getCriterioDePertenenciaID()).orElseThrow(() -> new RuntimeException("Criterio de Pertencion no encontrado con id: "));
      coleccionAModificar.setCriterioDePertenencia(criterio);
    }
    if (coleccionInput.getIdsFuentes() != null && !coleccionInput.getIdsFuentes().isEmpty()) {
      List<Fuente> fuentes = coleccionInput.getIdsFuentes().stream()
              .map(id -> fuenteRepositorio.findById(id)
                      .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + id)))
              .collect(Collectors.toList());

      coleccionAModificar.setFuentes(fuentes);
    }
    if (coleccionInput.getAlgoritmoConsenso() != null) {
      coleccionAModificar.setAlgoritmoConsenso(coleccionInput.getAlgoritmoConsenso());
    }
    coleccionAModificar.incrementarContadorModificaciones();
    this.coleccionRepositorio.save(coleccionAModificar);
    return coleccionAModificar.toDTO();
  }

  @Override
  @Transactional
  public ColeccionOutputDTO agregarFuente(String handle, Long idFuente) {
    Coleccion coleccion = this.coleccionRepositorio.findByHandle(handle);
    Fuente fuente = this.fuenteRepositorio.findById(idFuente).orElseThrow();
    coleccion.agregarFuente(fuente);
    this.coleccionRepositorio.save(coleccion);
    return coleccion.toDTO();
  }

  @Override
  @Transactional
  public ColeccionOutputDTO quitarFuente(String handle, Long idFuente) {
    Coleccion coleccion = this.coleccionRepositorio.findByHandle(handle);
    Fuente fuente = this.fuenteRepositorio.findById(idFuente).orElseThrow();
    coleccion.quitarFuente(fuente);
    this.coleccionRepositorio.save(coleccion);
    return coleccion.toDTO();
  }
}
