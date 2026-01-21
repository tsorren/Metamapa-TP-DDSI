package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Permiso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesPermisosDTO {
  private String username;
  private Rol rol;
  private List<Permiso> permisos;
}