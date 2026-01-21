package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

@Data
public class ContribuyenteInputDTO {
  private Long id;
  private String nombre;
  private String apellido;
  private String edad;
  private String mail;
  private String password;
  private String username;
}
