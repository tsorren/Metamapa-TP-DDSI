package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InformacionFiltrado {
  String categoria;
  String tipoOrigen;
  LocalDateTime fechaReporteDesde;
  LocalDateTime fechaReporteHasta;
  LocalDate fechaAcontecimientoDesde;
  LocalDate fechaAcontecimientoHasta;
  Double latitud;
  Double longitud;
  Double radio;
  Boolean navegacionCurada;
}
