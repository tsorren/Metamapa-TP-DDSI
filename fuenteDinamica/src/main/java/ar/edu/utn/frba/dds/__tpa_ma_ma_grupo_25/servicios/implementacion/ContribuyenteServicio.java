package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContribuyenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j // Logger
@Service
public class ContribuyenteServicio implements IContribuyenteServicio {
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;

  public ContribuyenteServicio(IContribuyenteRepositorioBD contribuyenteRepositorio) {
    this.contribuyenteRepositorio = contribuyenteRepositorio;
  }

  @Override
  @Transactional
  public ContribuyenteOutputDTO crear(Long idContribuyente, String nombre, String apellido, Integer edad) {
    Contribuyente contribuyente = Contribuyente.crear(idContribuyente, nombre, apellido, edad);
    contribuyenteRepositorio.save(contribuyente);

    log.info("<CREACION> Contribuyente creado con id {}", contribuyente.getId());
    return contribuyente.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public ContribuyenteOutputDTO findById(Long idContribuyente) {
    return contribuyenteRepositorio.findById(idContribuyente).orElseThrow().toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public List<HechoOutputDTO> hechosDeContribuyente(Long idContribuyente) {
    return contribuyenteRepositorio.findById(idContribuyente).orElseThrow()
            .getHechosCreados().stream().map(Hecho::toDTO).toList();
  }
}
