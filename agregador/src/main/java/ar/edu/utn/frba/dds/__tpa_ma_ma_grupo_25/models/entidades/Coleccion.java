package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ColeccionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "colecciones")
public class Coleccion {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String handle;

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descripcion")
  private String descripcion;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
          name = "coleccion_fuente",
          joinColumns = @JoinColumn(name = "coleccion_handle", referencedColumnName = "handle"),
          inverseJoinColumns = @JoinColumn(name = "fuente_id", referencedColumnName = "id")
  )
  private List<Fuente> fuentes;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "criterio_id", referencedColumnName = "id")
  private CriterioDePertenencia criterioDePertenencia;

  @Enumerated(EnumType.STRING)
  @Column(name = "algoritmo_de_consenso")
  private TipoConsenso algoritmoConsenso;

  @Column(name = "contador_modificaciones")
  @Builder.Default
  private Long contadorModificaciones = 0L;

  public static Coleccion crear(String unTitulo, String descripcion, CriterioDePertenencia criterioDePertenencia, List<Fuente> fuentes, TipoConsenso algoritmoConsenso) {
    return Coleccion.builder()
            .titulo(unTitulo)
            .descripcion(descripcion)
            .criterioDePertenencia(criterioDePertenencia)
            .fuentes(fuentes)
            .algoritmoConsenso(algoritmoConsenso)
            .contadorModificaciones(0L)
            .build();
  }

  public void incrementarContadorModificaciones() {
    this.contadorModificaciones++;
  }

  public void agregarFuente(Fuente fuente) {
    fuentes.add(fuente);
  }

  public void quitarFuente(Fuente fuente) {
    fuentes.remove(fuente);
  }

  public ColeccionOutputDTO toDTO() {
    return new ColeccionOutputDTO(
            this.handle,
            this.titulo,
            this.descripcion,
            this.fuentes.stream().map(Fuente::toDTO).collect(Collectors.toList()),
            this.criterioDePertenencia != null ? this.criterioDePertenencia.toDTO() : null,
            this.algoritmoConsenso
    );
  }

}
