package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.HechoServicio;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Contribuyente")
public class Contribuyente {
  @Id
  private Long id;

  @Column(name = "nombre")
  private String nombre;

  @Column(name = "apellido")
  private String apellido;

  @Column(name = "edad")
  private Integer edad;

  @OneToMany
  @JoinColumn(name = "hechos_creado", referencedColumnName = "id")
  private List<Hecho> hechosCreados;

  public static Contribuyente crear(Long id) {
    return Contribuyente
            .builder()
            .id(id)
            .nombre(null)
            .apellido(null)
            .edad(null)
            .hechosCreados(new ArrayList<>())
            .build();
  }
  public static Contribuyente crear(Long id, String nombre) {
    return Contribuyente
            .builder()
            .id(id)
            .nombre(nombre)
            .apellido(null)
            .edad(null)
            .hechosCreados(new ArrayList<>())
            .build();
  }

  public static Contribuyente crear(Long id, String nombre, String apellido) {
    return Contribuyente
            .builder()
            .id(id)
            .nombre(nombre)
            .apellido(apellido)
            .edad(null)
            .hechosCreados(new ArrayList<>())
            .build();
  }

  public static Contribuyente crear(Long id, String nombre, String apellido, Integer edad) {
    return Contribuyente
            .builder()
            .id(id)
            .nombre(nombre)
            .apellido(apellido)
            .edad(edad)
            .hechosCreados(new ArrayList<>())
            .build();
  }
  /*
  public Boolean creoHecho(Long idHecho) {
    return hechosCreados.contains(idHecho);
  }
  */

  public void agregarHecho(Hecho hecho) {
    hechosCreados.add(hecho);
  }

  public ContribuyenteOutputDTO toDTO() {
      return new ContribuyenteOutputDTO(
              id,
              nombre,
              apellido,
              edad
      );

  }
}
