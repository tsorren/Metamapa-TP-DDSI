package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class FuentesDTOForm {
  private List<FuenteDTO> fuentesSeleccionadas;

  public FuentesDTOForm() {
    fuentesSeleccionadas = new ArrayList<FuenteDTO>();
  }
}
