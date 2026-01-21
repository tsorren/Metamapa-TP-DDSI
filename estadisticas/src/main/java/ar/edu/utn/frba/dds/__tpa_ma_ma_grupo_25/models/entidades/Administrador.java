package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "administrador")
public class Administrador {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // ej Admin1
}
