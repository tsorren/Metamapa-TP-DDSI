package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
  private Double latitud;
  private Double longitud;
}
