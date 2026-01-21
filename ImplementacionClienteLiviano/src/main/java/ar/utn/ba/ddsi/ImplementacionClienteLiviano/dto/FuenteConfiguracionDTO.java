package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuenteConfiguracionDTO {
  private Long id;
  private String nombre;
  private String tipo; // ESTATICA, DINAMICA, PROXY
  private Long coleccionId;
  private LocalDateTime fechaConfiguracion;
  private String configuracion; // Path del archivo, URL, etc.
  private Long fuenteExternaId; // ID en el microservicio externo

  // Campos calculados para la vista
  private String nombreColeccion;
  private String handleColeccion;
  private String estadoImportacion;
  private Integer cantidadHechos;

  // Constructor personalizado para los campos principales (sin campos calculados)
  public FuenteConfiguracionDTO(Long id, String nombre, String tipo, Long coleccionId,
                                LocalDateTime fechaConfiguracion, String configuracion,
                                Long fuenteExternaId) {
    this.id = id;
    this.nombre = nombre;
    this.tipo = tipo;
    this.coleccionId = coleccionId;
    this.fechaConfiguracion = fechaConfiguracion;
    this.configuracion = configuracion;
    this.fuenteExternaId = fuenteExternaId;
  }
}
