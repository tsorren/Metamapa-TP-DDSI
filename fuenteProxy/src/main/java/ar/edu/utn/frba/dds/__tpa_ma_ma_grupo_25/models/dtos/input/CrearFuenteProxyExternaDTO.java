package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class CrearFuenteProxyExternaDTO {
  private String url;
  private String email;
  private String password;
  private String nombre;
}

