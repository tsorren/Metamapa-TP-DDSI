package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HechoOutputDTO {
    private Long identificador;
    private String titulo;
    private String descripcion;
    private CategoriaOutputDTO categoria;
    private UbicacionOutputDTO ubicacion;
    private LocalDate fechaDelHecho;
    private LocalDateTime fechaDeCarga;
    private LocalDateTime fechaUltimaModificacion;
    private MultimediaOutputDTO contenido;
    private String origen; // TipoOrigen como String
    private List<EtiquetaDTO> etiquetas;
    private Boolean visible;
}
