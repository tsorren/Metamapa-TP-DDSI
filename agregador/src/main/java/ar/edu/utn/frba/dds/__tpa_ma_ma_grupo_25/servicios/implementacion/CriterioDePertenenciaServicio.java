package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CriterioDePertenenciaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICriteriosPertenenciaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFiltroRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ICriterioDePertenenciaServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j // Logger
@Service
public class CriterioDePertenenciaServicio implements ICriterioDePertenenciaServicio {
  private final ICriteriosPertenenciaRepositorioBD criterioRepositorio;
  private final IFiltroRepositorioBD filtroRepositorio;

  public CriterioDePertenenciaServicio(
          ICriteriosPertenenciaRepositorioBD criterioRepositorio,
          IFiltroRepositorioBD filtroRepositorio
  ) {
    this.criterioRepositorio = criterioRepositorio;
    this.filtroRepositorio = filtroRepositorio;
  }

  @Override
  @Transactional(readOnly = true)
  public List<CriterioDePertenenciaOutputDTO> encontrarTodos() {
    return criterioRepositorio.findAll().stream().map(CriterioDePertenencia::toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CriterioDePertenenciaOutputDTO encontrarPorId(Long id) {
    CriterioDePertenencia criterio = criterioRepositorio.findById(id)
        .orElseThrow(() -> new RuntimeException("Criterio de pertenencia no encontrado con id: " + id));
    return criterio.toDTO();
  }

  @Override
  @Transactional
  public CriterioDePertenenciaOutputDTO crear() {
    CriterioDePertenencia criterio = new CriterioDePertenencia();
    criterioRepositorio.save(criterio);
    log.info("<CREACION> Criterio de Pertenencia con id {}", criterio.getId());
    return criterio.toDTO();
  }

  @Override
  @Transactional
  public CriterioDePertenenciaOutputDTO agregarFiltro(Long idCriterio, Long idFiltro) {
    CriterioDePertenencia criterio = criterioRepositorio.findById(idCriterio).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: "));
    criterio.agregarFiltro(filtroRepositorio.findById(idFiltro).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: ")));
    return criterio.toDTO();
  }

  @Override
  @Transactional
  public CriterioDePertenenciaOutputDTO quitarFiltro(Long idCriterio, Long idFiltro) {
    CriterioDePertenencia criterio = criterioRepositorio.findById(idCriterio).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: "));
    criterio.quitarFiltro(filtroRepositorio.findById(idFiltro).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: ")));
    return criterio.toDTO();
  }
}
