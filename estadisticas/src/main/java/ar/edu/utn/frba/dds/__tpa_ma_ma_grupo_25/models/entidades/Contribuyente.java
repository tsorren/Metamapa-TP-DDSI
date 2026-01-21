package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contribuyentes")
public class Contribuyente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "apellido")
  private String apellido;

  @Column(name = "edad")
  private Integer edad;

  public static Contribuyente crear(String nombre) {
    return Contribuyente
            .builder()
            .nombre(nombre)
            .apellido(null)
            .edad(null)
            .build();
  }

  public static Contribuyente crear(String nombre, String apellido) {
    return Contribuyente
            .builder()
            .nombre(nombre)
            .apellido(apellido)
            .edad(null)
            .build();
  }

  public static Contribuyente crear(String nombre, String apellido, Integer edad) {
    return Contribuyente
            .builder()
            .nombre(nombre)
            .apellido(apellido)
            .edad(edad)
            .build();
  }
}