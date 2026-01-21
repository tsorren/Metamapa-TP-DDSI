package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;

public interface IAdministradorServicio {
  AdministradorOutputDTO crear(String username, String password);

  Administrador findByUsername(String username);
}
