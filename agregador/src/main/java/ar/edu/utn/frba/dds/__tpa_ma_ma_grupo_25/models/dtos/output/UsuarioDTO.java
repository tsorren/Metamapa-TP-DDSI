package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class UsuarioDTO {
  public final Long id;
  public final String username;
  public final String password;
  public final String rol;
  public final List<String> permisos;
}
