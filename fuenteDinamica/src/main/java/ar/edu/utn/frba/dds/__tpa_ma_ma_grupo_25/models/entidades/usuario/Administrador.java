package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Administrador")
public class Administrador {
  @Id
  public Long id; // ej Admin1

  @Column
  public String username;

  // @Column
  // public String password;


  public AdministradorOutputDTO toDTO() {
    return new AdministradorOutputDTO(this.id, this.username);
  }
}
