package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HechoExternoInput {
  private Long id;
  private String descripcion;
  private String titulo;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private OffsetDateTime fecha_hecho;
  private OffsetDateTime created_at;
  private OffsetDateTime updated_at;
}

