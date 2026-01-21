package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultimediaOutputDTO {
    private Long id;
    private String tipo; // "IMAGEN", "VIDEO", "AUDIO"
    private String url;
    private String descripcion;
}
