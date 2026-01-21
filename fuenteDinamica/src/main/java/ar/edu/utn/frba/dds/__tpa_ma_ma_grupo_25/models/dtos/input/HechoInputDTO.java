package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HechoInputDTO {
  private Long idContribuyente;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDelHecho;
  private Long idContenido;
}
