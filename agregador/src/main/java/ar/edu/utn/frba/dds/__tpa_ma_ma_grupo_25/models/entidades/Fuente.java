package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fuentes")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "colecciones"})
public class Fuente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToMany(mappedBy = "fuentes")
  private List<Coleccion> colecciones;

  @ManyToOne(optional = false) // muchas fuentes pueden usar el mismo módulo
  @JoinColumn(name = "modulo_id", nullable = false)
  private ModuloFuente modulo;

  @Column(name = "identificadorExterno", nullable = false)
  private Long identificadorExterno;

  @Column(name = "fecha_de_creacion", nullable = false)
  private LocalDateTime fechaDeCreacion;

  public Fuente(ModuloFuente modulo, Long identificadorExterno, LocalDateTime fechaDeCreacion) {
    this.modulo = modulo;
    this.identificadorExterno = identificadorExterno;
    this.fechaDeCreacion = fechaDeCreacion;
  }

  public FuenteOutputDTO toDTO() {
    return new FuenteOutputDTO(
            this.id,
            this.identificadorExterno,
            this.modulo != null ? this.modulo.toDTO() : null
    );
  }
}
