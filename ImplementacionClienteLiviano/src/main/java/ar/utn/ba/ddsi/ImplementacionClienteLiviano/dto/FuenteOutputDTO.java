package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuenteOutputDTO {
    private Long identificador;
    private Long identificadorExterno;
    private ModuloFuenteOutputDTO moduloFuente;
}

