package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.EtiquetaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Etiqueta;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IEtiquetaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IEtiquetaServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtiquetaServicio implements IEtiquetaServicio {
  private final IEtiquetaRepositorioBD etiquetaRepositorio;

  public EtiquetaServicio(IEtiquetaRepositorioBD etiquetaRepositorio) {
    this.etiquetaRepositorio = etiquetaRepositorio;
  }

  @Override
  @Transactional
  public EtiquetaOutputDTO crear(String nombre) {
    Etiqueta etiqueta = new Etiqueta(nombre);
    etiquetaRepositorio.save(etiqueta);
    return etiqueta.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public List<EtiquetaOutputDTO> encontrarTodas() {
    return etiquetaRepositorio.findAll().stream().map(Etiqueta::toDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public EtiquetaOutputDTO encontrarPorId(Long id) {
    return etiquetaRepositorio.findById(id).orElseThrow().toDTO();
  }
}
