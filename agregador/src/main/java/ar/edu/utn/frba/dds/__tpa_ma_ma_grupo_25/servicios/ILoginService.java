package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.RolesPermisosDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Usuario;

public interface ILoginService {
  Usuario autenticarUsuario(String username, String password);
  String generarAccessToken(String username);
  String generarRefreshToken(String username);
  RolesPermisosDTO obtenerRolesYPermisosUsuario(String username);
}
