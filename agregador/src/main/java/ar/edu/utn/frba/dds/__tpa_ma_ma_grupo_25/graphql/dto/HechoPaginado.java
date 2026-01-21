package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HechoPaginado<T> {
    private List<T> contenido;
    private Integer paginaActual;
    private Integer totalPaginas;
    private Long totalElementos;
    private Integer tamanio;
}

