package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginacionInput {
    private Integer pagina = 0;
    private Integer tamanio = 20;
}

