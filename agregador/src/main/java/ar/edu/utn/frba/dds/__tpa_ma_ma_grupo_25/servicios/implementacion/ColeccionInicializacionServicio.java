package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.FiltroUbicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IColeccionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICriteriosPertenenciaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFiltroRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Order(2) // Ejecutar DESPUÉS de ModuloFuenteInicializacionServicio
public class ColeccionInicializacionServicio {

  private final IColeccionRepositorioBD coleccionRepositorio;
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final ICriteriosPertenenciaRepositorioBD criteriosPertenenciaRepositorio;
  private final IFiltroRepositorioBD filtroRepositorio;

  public ColeccionInicializacionServicio(
          IColeccionRepositorioBD coleccionRepositorio,
          IFuenteRepositorioBD fuenteRepositorio,
          ICriteriosPertenenciaRepositorioBD criteriosPertenenciaRepositorio,
          IFiltroRepositorioBD filtroRepositorio
  ) {
    this.coleccionRepositorio = coleccionRepositorio;
    this.fuenteRepositorio = fuenteRepositorio;
    this.criteriosPertenenciaRepositorio = criteriosPertenenciaRepositorio;
    this.filtroRepositorio = filtroRepositorio;
  }

  @PostConstruct
  @Transactional
  public void inicializarColeccionPorDefecto() {
    log.info("📋 Verificando colección por defecto...");

    try {
      // Verificar si ya existe alguna colección con el título "Hechos en Bariloche"
      List<Coleccion> coleccionesExistentes = coleccionRepositorio.findAll();
      boolean existeColeccion = coleccionesExistentes.stream()
              .anyMatch(c -> "Hechos en Bariloche".equals(c.getTitulo()));

      if (existeColeccion) {
        log.info("✅ La colección 'Hechos en Bariloche' ya existe");
        log.info("📊 Total de colecciones: {}", coleccionesExistentes.size());
        return;
      }

      log.info("🔄 Creando colección por defecto...");

      // Crear un criterio de pertenencia CON filtro de ubicación para Bariloche
      CriterioDePertenencia criterio = new CriterioDePertenencia();

      // Agregar filtro de ubicación: Bariloche está en lat: -41.1335, lng: -71.3103
      // Radio de 150 km para capturar toda la región
      Ubicacion ubicacionBariloche = new Ubicacion(-41.1335, -71.3103);
      FiltroUbicacion filtroUbicacion = new FiltroUbicacion(ubicacionBariloche, 150.0);

      // IMPORTANTE: Guardar el filtro ANTES de agregarlo al criterio
      filtroRepositorio.save(filtroUbicacion);
      log.info("✅ Filtro de ubicación guardado en BD");

      criterio.agregarFiltro(filtroUbicacion);

      criteriosPertenenciaRepositorio.save(criterio);
      log.info("✅ Criterio de pertenencia creado con filtro de ubicación (Bariloche, radio 150km)");

      // Obtener todas las fuentes disponibles
      List<Fuente> todasLasFuentes = fuenteRepositorio.findAll();
      log.info("📊 Fuentes disponibles: {}", todasLasFuentes.size());

      if (todasLasFuentes.isEmpty()) {
        log.warn("⚠️ No hay fuentes disponibles. La colección se creará sin fuentes.");
        log.info("💡 Asegúrate de que los módulos de fuentes estén registrados");
      }

      // Crear la colección por defecto para hechos de Bariloche usando el método estático
      Coleccion coleccionDefault = Coleccion.crear(
              "Hechos en Bariloche",
              "Colección de todos los eventos y hechos reportados en la región de Bariloche y sus alrededores",
              criterio,
              todasLasFuentes,
              TipoConsenso.MAYORIA_SIMPLE
      );

      // Guardar y luego obtener el handle generado
      Coleccion coleccionGuardada = coleccionRepositorio.save(coleccionDefault);

      log.info("✅ Colección por defecto creada exitosamente:");
      log.info("   - Handle: {}", coleccionGuardada.getHandle());
      log.info("   - Título: {}", coleccionGuardada.getTitulo());
      log.info("   - Fuentes: {}", todasLasFuentes.size());

    } catch (Exception e) {
      log.error("❌ Error al crear la colección por defecto: {}", e.getMessage(), e);
    }
  }
}

