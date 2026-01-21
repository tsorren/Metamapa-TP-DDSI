package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.CriterioDePertenenciaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table
public class CriterioDePertenencia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Filtro> filtros;

  public CriterioDePertenencia() {
    this.filtros = new ArrayList<>();
  }

  public void agregarFiltro(Filtro nuevoFiltro) {
    this.filtros.add(nuevoFiltro);
  }

  public void quitarFiltro(Filtro nuevoFiltro) {
    this.filtros.remove(nuevoFiltro);
  }

  public List<Hecho> aplicar(List<Hecho> hechos) {
    return hechos.stream().filter(this::cumpleCon).collect(Collectors.toList());
  }

  public boolean cumpleCon(Hecho hecho) {
    return filtros.stream().allMatch(f -> f.cumpleCon(hecho));
  }

  public void generarFiltros(HechosFiltrandoInputDTO i, Boolean navegacionCurada, TipoConsenso tipoConsenso) {
    if (i.getCategoria().getNombre() != null) {
      this.agregarFiltro(new FiltroCategoria(i.getCategoria()));
    }
    if (i.getTipoOrigen() != null) {
      this.agregarFiltro(new FiltroTipoOrigen(i.getTipoOrigen()));
    }
    if (i.getFecha_reporte_desde() != null || i.getFecha_reporte_hasta() != null) {
      this.agregarFiltro(new FiltroCargaEntre(i.getFecha_reporte_desde(), i.getFecha_reporte_hasta()));
    }
    if (i.getFecha_acontecimiento_desde() != null | i.getFecha_acontecimiento_hasta() != null) {
      this.agregarFiltro(new FiltroFechaEntre(i.getFecha_acontecimiento_desde(), i.getFecha_acontecimiento_hasta()));
    }
    if (i.getUbicacion().getLatitud() != null && i.getUbicacion().getLongitud() != null && i.getRadio() != null) {
      this.agregarFiltro(new FiltroUbicacion(i.getUbicacion(), i.getRadio()));
    }
    if (navegacionCurada != null && navegacionCurada) {
      this.agregarFiltro(new FiltroCurado(tipoConsenso));
    }
  }

  public CriterioDePertenenciaOutputDTO toDTO() {
    return new CriterioDePertenenciaOutputDTO(
            this.id,
            this.filtros.stream().map(Filtro::toDTO).toList()
    );
  }
}
