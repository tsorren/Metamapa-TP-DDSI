package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.GeocodingService;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "hecho_procesado")
public class HechoProcesado {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "categoria")
  private String categoria;

  @Column(name = "provincia")
  private String provincia; // calculada a partir de lat/lon

  @Column(name = "fecha_hora")
  private LocalDateTime fechaHora;

  public HechoProcesado(String categoria, String provincia, LocalDateTime fechaHora) {
    this.categoria = categoria;
    this.provincia = provincia;
    this.fechaHora = fechaHora;
  }

  // Fábrica que construye desde el DTO
  public static HechoProcesado desdeDTO(HechoInputDTO dto, GeocodingService geocodingService) {
    HechoProcesado hecho = new HechoProcesado();
    hecho.categoria = dto.getCategoria().getNombre();
    hecho.fechaHora = dto.getFechaDeCarga();

    if (dto.getUbicacion() != null) {
      double lat = dto.getUbicacion().getLatitud();
      double lon = dto.getUbicacion().getLongitud();
      hecho.provincia = geocodingService.obtenerProvincia(lat, lon);
    } else {
      hecho.provincia = "Desconocida";
    }

    return hecho;
  }
}

