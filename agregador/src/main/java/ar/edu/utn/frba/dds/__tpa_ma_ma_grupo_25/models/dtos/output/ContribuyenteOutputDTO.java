package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.Data;

@Data
public class ContribuyenteOutputDTO {
  private final Long id;

  private final String nombre;

  private final String apellido;

  private final Integer edad;

  private final String username;

  private final String password;

  private final String mail;
}