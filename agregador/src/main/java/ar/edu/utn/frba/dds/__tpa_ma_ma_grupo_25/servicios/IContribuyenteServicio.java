package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;

public interface IContribuyenteServicio {
  ContribuyenteOutputDTO crear(ContribuyenteInputDTO inputDTO);
  Contribuyente findByUsername(String username);
  Contribuyente findByUserEmail(String userEmail);
}
