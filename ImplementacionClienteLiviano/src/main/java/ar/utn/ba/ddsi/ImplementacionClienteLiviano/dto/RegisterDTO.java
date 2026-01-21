package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
  public String username;
  public String email;
  public String password;
  public String nombre;
  public String apellido;
  public String edad;
}
