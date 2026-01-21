package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.conversores.PathConverter;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.lectores.LectorCsv;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Fuente_Estatica")
public class FuenteEstatica {
  @Transient
  private static DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @Convert(converter = PathConverter.class)
  @Column(name = "ruta ")
  private Path ruta;

  @Column(name = "fecha_de_creacion")
  private LocalDateTime fechaDeCreacion;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "fuente", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Hecho> hechos = new ArrayList<>();


  public FuenteEstatica(Path ruta) {
    this.ruta = ruta;
    this.fechaDeCreacion = null;
  }
  
  public FuenteOutputDTO toDTO() {
    return new FuenteOutputDTO(this.id, this.fechaDeCreacion);
  }

}