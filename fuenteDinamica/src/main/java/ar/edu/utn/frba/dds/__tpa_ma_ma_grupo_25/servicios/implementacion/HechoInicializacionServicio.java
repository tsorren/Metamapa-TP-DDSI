package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HechoInicializacionServicio {

  private final IHechoRepositorioBD hechoRepositorio;
  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final IFuenteRepositorioBD fuenteRepositorio;

  public HechoInicializacionServicio(
          IHechoRepositorioBD hechoRepositorio,
          ICategoriaRepositorioBD categoriaRepositorio,
          IFuenteRepositorioBD fuenteRepositorio
  ) {
    this.hechoRepositorio = hechoRepositorio;
    this.categoriaRepositorio = categoriaRepositorio;
    this.fuenteRepositorio = fuenteRepositorio;
  }

  @PostConstruct
  @Transactional
  public void inicializarHechosDeEjemplo() {
    // Verificar si ya existen hechos
    long cantidadHechos = hechoRepositorio.count();

    if (cantidadHechos > 0) {
      log.info("✅ Ya existen {} hechos en Fuente Dinámica. No se crearán ejemplos.", cantidadHechos);
      return;
    }

    log.info("📋 Creando hechos de ejemplo en Fuente Dinámica...");

    try {
      // 1. Crear o obtener FuenteDinamica
      FuenteDinamica fuente = obtenerOCrearFuenteDinamica();
      log.info("✅ Fuente Dinámica ID: {}", fuente.getId());

      // 2. Obtener o crear categoría "Incendio"
      Categoria categoriaIncendio = obtenerOCrearCategoria("Incendio");

      // 3. Crear hechos y asociarlos a la fuente
      crearYAsociarHecho(
              fuente,
              "Incendio en Hotel",
              "Incendio reportado en un hotel céntrico de Bariloche. Se evacuó a los huéspedes sin heridos.",
              categoriaIncendio,
              -41.1335, -71.3103,
              LocalDate.now().minusDays(5)
      );

      crearYAsociarHecho(
              fuente,
              "Incendio en Comisaría",
              "Incendio en comisaría local de Bariloche. Bomberos controlaron el fuego rápidamente.",
              categoriaIncendio,
              -41.1457, -71.3082,
              LocalDate.now().minusDays(3)
      );

      crearYAsociarHecho(
              fuente,
              "Incendio en Iglesia",
              "Incendio en iglesia histórica del centro. Daños estructurales menores.",
              categoriaIncendio,
              -41.1289, -71.3186,
              LocalDate.now().minusDays(2)
      );

      crearYAsociarHecho(
              fuente,
              "Incendio en Bar",
              "Incendio en bar céntrico durante la madrugada. Sin víctimas reportadas.",
              categoriaIncendio,
              -41.1401, -71.3052,
              LocalDate.now().minusDays(1)
      );

      // Guardar la fuente con todos los hechos asociados
      fuenteRepositorio.save(fuente);

      log.info("✅ Se crearon 4 hechos y se asociaron a Fuente Dinámica ID {}", fuente.getId());

    } catch (Exception e) {
      log.error("❌ Error al crear hechos de ejemplo: {}", e.getMessage(), e);
    }
  }

  private FuenteDinamica obtenerOCrearFuenteDinamica() {
    List<FuenteDinamica> fuentes = fuenteRepositorio.findAll();

    if (!fuentes.isEmpty()) {
      FuenteDinamica fuente = fuentes.get(0);
      // Inicializar la colección lazy dentro de la transacción para evitar LazyInitializationException
      if (fuente.getHechosAsociados() == null) {
        fuente.setHechosAsociados(new ArrayList<>());
      } else {
        // Forzar la carga de la colección lazy
        int size = fuente.getHechosAsociados().size();
        log.info("📋 Usando Fuente Dinámica existente ID: {} con {} hechos", fuente.getId(), size);
      }
      return fuente;
    }

    // Crear nueva fuente con lista inicializada
    FuenteDinamica nuevaFuente = new FuenteDinamica();
    nuevaFuente.setHechosAsociados(new ArrayList<>());
    fuenteRepositorio.save(nuevaFuente);
    log.info("➕ Fuente Dinámica creada con ID: {}", nuevaFuente.getId());
    return nuevaFuente;
  }

  private void crearYAsociarHecho(FuenteDinamica fuente, String titulo, String descripcion,
                                   Categoria categoria, double latitud, double longitud, LocalDate fecha) {
    Ubicacion ubicacion = new Ubicacion(latitud, longitud);

    Hecho hecho = Hecho.crear(
            titulo,
            descripcion,
            categoria,
            ubicacion,
            fecha,
            TipoOrigen.MANUAL,
            null // sin multimedia
    );

    // Inicializar la lista de fuentes si es null
    if (hecho.getFuentes() == null) {
      hecho.setFuentes(new ArrayList<>());
    }

    // Asociar el hecho a la fuente
    hecho.getFuentes().add(fuente);

    // Guardar el hecho
    hechoRepositorio.save(hecho);

    // Agregar el hecho a la lista de la fuente (ahora dentro de la transacción)
    fuente.getHechosAsociados().add(hecho);

    log.info("  ➕ Creado y asociado: {} en ({}, {})", titulo, latitud, longitud);
  }

  private Categoria obtenerOCrearCategoria(String nombre) {
    Categoria categoriaExistente = categoriaRepositorio.findByNombre(nombre);

    if (categoriaExistente != null) {
      return categoriaExistente;
    }

    // Crear nueva categoría
    Categoria nuevaCategoria = new Categoria();
    nuevaCategoria.setNombre(nombre);
    categoriaRepositorio.save(nuevaCategoria);

    log.info("  ➕ Categoría creada: {}", nombre);
    return nuevaCategoria;
  }
}

