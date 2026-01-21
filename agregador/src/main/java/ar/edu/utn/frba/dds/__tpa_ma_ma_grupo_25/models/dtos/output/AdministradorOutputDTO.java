package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

@Data
public class AdministradorOutputDTO {
  private final Long identificador;
  private final String username;
  private final String password;
}
