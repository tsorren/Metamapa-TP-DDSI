package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasFuenteEstaticaDTO {
    private Integer fuentesCreadas;  // Total de fuentes estáticas creadas
    private Integer hechosImportados;  // Total de hechos en la base de datos (importados de CSV)
}

