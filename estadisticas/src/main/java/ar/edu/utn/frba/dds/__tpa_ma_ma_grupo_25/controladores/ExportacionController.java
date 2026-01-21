package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ExportacionCSVServicio;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/exportaciones")
public class ExportacionController {

  private final ExportacionCSVServicio exportService;

  public ExportacionController(ExportacionCSVServicio exportService) {
    this.exportService = exportService;
  }

  @GetMapping(value = "/provincia", produces = "text/csv")
  public void exportProvincia(HttpServletResponse response) throws IOException {
    String filePath = "/tmp/provincia_estadisticas.csv";
    exportService.exportarProvinciaEstadisticas(filePath);

    response.setHeader("Content-Disposition", "attachment; filename=provincia_estadisticas.csv");
    response.setContentType("text/csv");
    Files.copy(Path.of(filePath), response.getOutputStream());
  }

  @GetMapping(value = "/categoria", produces = "text/csv")
  public void exportCategoria(HttpServletResponse response) throws IOException {
    String filePath = "/tmp/categoria_estadisticas.csv";
    exportService.exportarCategoriaEstadisticas(filePath);

    response.setHeader("Content-Disposition", "attachment; filename=categoria_estadisticas.csv");
    response.setContentType("text/csv");
    Files.copy(Path.of(filePath), response.getOutputStream());
  }

  @GetMapping(value = "/provincia-categoria", produces = "text/csv")
  public void exportProvinciaCategoria(HttpServletResponse response) throws IOException {
    String filePath = "/tmp/provincia_categoria_estadisticas.csv";
    exportService.exportarProvinciaCategoriaEstadisticas(filePath);

    response.setHeader("Content-Disposition", "attachment; filename=provincia_categoria_estadisticas.csv");
    response.setContentType("text/csv");
    Files.copy(Path.of(filePath), response.getOutputStream());
  }

  @GetMapping(value = "/hora-categoria", produces = "text/csv")
  public void exportHoraCategoria(HttpServletResponse response) throws IOException {
    String filePath = "/tmp/hora_categoria_estadisticas.csv";
    exportService.exportarHoraCategoriaEstadisticas(filePath);

    response.setHeader("Content-Disposition", "attachment; filename=hora_categoria_estadisticas.csv");
    response.setContentType("text/csv");
    Files.copy(Path.of(filePath), response.getOutputStream());
  }

  @GetMapping(value = "/spam", produces = "text/csv")
  public void exportSpam(HttpServletResponse response) throws IOException {
    String filePath = "/tmp/spam_estadisticas.csv";
    exportService.exportarSpamEstadisticas(filePath);

    response.setHeader("Content-Disposition", "attachment; filename=spam_estadisticas.csv");
    response.setContentType("text/csv");
    Files.copy(Path.of(filePath), response.getOutputStream());
  }
}
