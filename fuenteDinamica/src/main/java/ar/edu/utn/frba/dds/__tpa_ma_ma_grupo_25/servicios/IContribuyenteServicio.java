package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;

import java.util.List;

public interface IContribuyenteServicio {
  ContribuyenteOutputDTO crear(Long idContribuyente, String nombre, String apellido, Integer edad);
  ContribuyenteOutputDTO findById(Long idContribuyente);
  List<HechoOutputDTO> hechosDeContribuyente(Long idContribuyente);

}
