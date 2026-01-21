package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas.CategoriaEstadistica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.estadisitcas.ProvinciaEstadistica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.*;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class ExportacionCSVServicio {
  private final ProvinciaEstadisticaRepositorioBD provinciaRepo;
  private final CategoriaEstadisticaRepositorioBD categoriaRepo;
  private final ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo;
  private final HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo;
  private final SolicitudSpamEstadisticaRepositorioBD spamRepo;

  public ExportacionCSVServicio(
          ProvinciaEstadisticaRepositorioBD provinciaRepo,
          CategoriaEstadisticaRepositorioBD categoriaRepo,
          ProvinciaCategoriaEstadisticaRepositorioBD provinciaCategoriaRepo,
          HoraCategoriaEstadisticaRepositorioBD horaCategoriaRepo,
          SolicitudSpamEstadisticaRepositorioBD spamRepo
  ) {
    this.provinciaRepo = provinciaRepo;
    this.categoriaRepo = categoriaRepo;
    this.provinciaCategoriaRepo = provinciaCategoriaRepo;
    this.horaCategoriaRepo = horaCategoriaRepo;
    this.spamRepo = spamRepo;
  }

  public void exportarProvinciaEstadisticas(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("provincia,cantidad_hechos,fecha_calculo\n");
      for (ProvinciaEstadistica e : provinciaRepo.findAll()) {
        writer.append(e.getProvincia()).append(",")
                .append(String.valueOf(e.getCantidadHechos())).append(",")
                .append(String.valueOf(e.getFechaCalculo())).append("\n");
      }
    }
  }

  public void exportarCategoriaEstadisticas(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("categoria,cantidad_hechos,fecha_calculo\n");
      for (CategoriaEstadistica e : categoriaRepo.findAll()) {
        writer.append(e.getCategoria()).append(",")
                .append(String.valueOf(e.getCantidadHechos())).append(",")
                .append(String.valueOf(e.getFechaCalculo())).append("\n");
      }
    }
  }

  public void exportarProvinciaCategoriaEstadisticas(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("provincia,categoria,cantidad_hechos,fecha_calculo\n");
      for (var e : provinciaCategoriaRepo.findAll()) {
        writer.append(e.getProvincia()).append(",")
                .append(e.getCategoria()).append(",")
                .append(String.valueOf(e.getCantidadHechos())).append(",")
                .append(String.valueOf(e.getFechaCalculo())).append("\n");
      }
    }
  }

  public void exportarHoraCategoriaEstadisticas(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("hora,categoria,cantidad_hechos,fecha_calculo\n");
      for (var e : horaCategoriaRepo.findAll()) {
        writer.append(String.valueOf(e.getHoraDelDia())).append(",")
                .append(e.getCategoria()).append(",")
                .append(String.valueOf(e.getCantidadHechos())).append(",")
                .append(String.valueOf(e.getFechaCalculo())).append("\n");
      }
    }
  }

  public void exportarSpamEstadisticas(String filePath) throws IOException {
    try (FileWriter writer = new FileWriter(filePath)) {
      writer.append("fecha_solicitud,ip,cantidad_solicitudes\n");
      for (var e : spamRepo.findAll()) {
        writer.append(String.valueOf(e.getFechaCalculo())).append(",")
                .append(e.getId().toString()).append(",")
                .append(String.valueOf(e.getCantidadSpam())).append("\n");
      }
    }
  }
}
