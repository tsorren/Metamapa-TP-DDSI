package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
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
  @Column
  String username;
  @Column
  String password;

  @Column
  String mail;

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

  public static Contribuyente crear(String username, String password, String mail) {
    return Contribuyente
        .builder()
        .username(username)
        .password(password)
        .mail(mail)
        .build();
  }


  public ContribuyenteOutputDTO toDTO() {
    return new ContribuyenteOutputDTO(this.id, this.nombre, this.apellido, this.edad, this.username, this.password, this.mail);
  }
}