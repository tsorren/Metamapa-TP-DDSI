package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteEstaticaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ImportacionProgressTracker;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.lectores.LectorCsv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Servicio dedicado para la importación asíncrona de hechos.
 *
 * Usa transacciones programáticas y procesamiento paralelo para:
 * 1. Persistir todas las categorías únicas PRIMERO
 * 2. Luego persistir los hechos en paralelo con referencias a categorías ya existentes
 */
@Slf4j
@Service
public class ImportacionAsincronaServicio {
  private static final LectorCsv lectorCsv = new LectorCsv();
  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final int THREADS_PARALELOS = 4;
  private static final int BATCH_SIZE = 250;

  private final IFuenteEstaticaRepositorioBD fuenteRepositorio;
  private final IHechoRepositorioBD hechoRepositorio;
  private final NormalizadorCategoria normalizadorCategoria;
  private final ImportacionProgressTracker progressTracker;
  private final TransactionTemplate transactionTemplate;

  public ImportacionAsincronaServicio(
          IFuenteEstaticaRepositorioBD fuenteRepositorio,
          IHechoRepositorioBD hechoRepositorio,
          NormalizadorCategoria normalizadorCategoria,
          ImportacionProgressTracker progressTracker,
          PlatformTransactionManager transactionManager) {
    this.fuenteRepositorio = fuenteRepositorio;
    this.hechoRepositorio = hechoRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
    this.progressTracker = progressTracker;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  /**
   * Importa hechos de forma asíncrona.
   * NO usa @Transactional - maneja transacciones programáticamente.
   */
  @Async("importacionExecutor")
  public void importarHechosAsync(Long fuenteId) {
    log.info("<ASYNC> Iniciando importación asíncrona para fuente {}", fuenteId);
    try {
      importarHechosInterno(fuenteId);
    } catch (Exception e) {
      log.error("<ASYNC> Error en importación asíncrona para fuente {}: {}", fuenteId, e.getMessage(), e);
      progressTracker.finalizar(fuenteId, false, "Error: " + e.getMessage());
    }
  }

  /**
   * Importa hechos de forma síncrona.
   */
  public List<HechoOutputDTO> importarHechosSync(Long fuenteId) {
    return importarHechosInterno(fuenteId);
  }

  private List<HechoOutputDTO> importarHechosInterno(Long fuenteId) {
    List<HechoOutputDTO> resultados = Collections.synchronizedList(new ArrayList<>());
    AtomicInteger procesados = new AtomicInteger(0);
    AtomicInteger errores = new AtomicInteger(0);

    try {
      // PASO 1: Obtener la ruta del archivo
      Path rutaArchivo = transactionTemplate.execute(status -> {
        FuenteEstatica fuente = fuenteRepositorio.findById(fuenteId)
                .orElseThrow(() -> new IllegalArgumentException("Fuente no encontrada: " + fuenteId));
        return fuente.getRuta();
      });

      log.info("<PROCESO> Iniciando importación para fuente {} desde {}", fuenteId, rutaArchivo);

      // PASO 2: Leer el CSV
      List<HashMap<String, String>> filasCsv = lectorCsv.leer(rutaArchivo);
      int totalFilas = filasCsv.size();
      log.info("<PROCESO> Total de filas del CSV: {}", totalFilas);

      if (totalFilas == 0) {
        progressTracker.iniciar(fuenteId, 0);
        progressTracker.finalizar(fuenteId, true, "No hay hechos para importar");
        return resultados;
      }

      // PASO 3: Extraer y persistir categorías únicas
      Set<String> nombresCategoriasUnicos = filasCsv.stream()
              .map(fila -> fila.get("Categoría"))
              .filter(cat -> cat != null && !cat.trim().isEmpty())
              .map(String::trim)
              .collect(Collectors.toSet());

      log.info("<PROCESO> Categorías únicas: {}", nombresCategoriasUnicos);

      // PASO 4: Persistir categorías en una transacción
      transactionTemplate.execute(status -> {
        int categoriasCreadas = 0;
        int categoriasError = 0;
        for (String nombreCategoria : nombresCategoriasUnicos) {
          try {
            normalizadorCategoria.normalizar(new Categoria(nombreCategoria));
            categoriasCreadas++;
          } catch (Exception e) {
            log.error("<CATEGORIAS> Error procesando '{}': {}", nombreCategoria, e.getMessage());
            categoriasError++;
          }
        }
        log.info("<CATEGORIAS> Procesadas: {} creadas/encontradas, {} errores", categoriasCreadas, categoriasError);
        return null;
      });

      // Inicializar tracker
      progressTracker.iniciar(fuenteId, totalFilas);

      // PASO 5: Procesar hechos en paralelo por batches
      ExecutorService executor = Executors.newFixedThreadPool(THREADS_PARALELOS);

      try {
        // Dividir en batches
        List<List<HashMap<String, String>>> batches = particionarEnBatches(filasCsv, BATCH_SIZE);
        log.info("<PROCESO> Procesando {} batches de hasta {} hechos cada uno", batches.size(), BATCH_SIZE);

        // Procesar batches en paralelo
        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() ->
                    procesarBatch(batch, fuenteId, resultados, procesados, errores), executor))
                .collect(Collectors.toList());

        // Esperar a que todos terminen
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

      } finally {
        executor.shutdown();
      }

      int totalProcesados = procesados.get();
      int totalErrores = errores.get();

      log.info("<PROCESO> Fuente {} finalizó: {} exitosos, {} errores", fuenteId, totalProcesados, totalErrores);
      progressTracker.finalizar(fuenteId, totalErrores == 0,
              String.format("Importados %d hechos (%d errores)", totalProcesados, totalErrores));

      return resultados;

    } catch (Exception e) {
      log.error("Error al importar hechos de fuente {}: {}", fuenteId, e.getMessage(), e);
      progressTracker.finalizar(fuenteId, false, "Error: " + e.getMessage());
      throw new RuntimeException("Error al importar hechos: " + e.getMessage(), e);
    }
  }

  /**
   * Procesa un batch de filas en una única transacción.
   */
  private void procesarBatch(List<HashMap<String, String>> batch, Long fuenteId,
                              List<HechoOutputDTO> resultados, AtomicInteger procesados,
                              AtomicInteger errores) {
    try {
      List<HechoOutputDTO> batchResultados = transactionTemplate.execute(status -> {
        List<HechoOutputDTO> resultadosBatch = new ArrayList<>();

        // Cargar la fuente UNA SOLA VEZ para todo el batch
        FuenteEstatica fuente = fuenteRepositorio.findById(fuenteId)
                .orElseThrow(() -> new IllegalArgumentException("Fuente no encontrada"));

        for (HashMap<String, String> fila : batch) {
          try {
            // Obtener categoría del cache del normalizador
            String nombreCategoria = fila.get("Categoría");
            Categoria categoria = null;
            if (nombreCategoria != null && !nombreCategoria.trim().isEmpty()) {
              categoria = normalizadorCategoria.normalizar(new Categoria(nombreCategoria.trim()));
            }

            // Crear el hecho
            Hecho hecho = Hecho.crear(
                    fila.get("Título"),
                    fila.get("Descripción"),
                    categoria,
                    new Ubicacion(
                            Double.parseDouble(fila.get("Latitud")),
                            Double.parseDouble(fila.get("Longitud"))
                    ),
                    LocalDate.parse(fila.get("Fecha del hecho"), FORMATO_FECHA),
                    TipoOrigen.DATASET
            );
            hecho.setFuente(fuente);

            hecho = hechoRepositorio.save(hecho);
            resultadosBatch.add(HechoServicio.hechoOutputDTO(hecho));

          } catch (Exception e) {
            errores.incrementAndGet();
          }
        }

        return resultadosBatch;
      });

      if (batchResultados != null) {
        resultados.addAll(batchResultados);
        procesados.addAndGet(batchResultados.size());
        progressTracker.actualizar(fuenteId, procesados.get(), errores.get());
      }

    } catch (Exception e) {
      log.error("Error procesando batch: {}", e.getMessage());
      errores.addAndGet(batch.size());
    }
  }

  /**
   * Divide una lista en batches de tamaño especificado.
   */
  private <T> List<List<T>> particionarEnBatches(List<T> lista, int tamañoBatch) {
    List<List<T>> batches = new ArrayList<>();
    for (int i = 0; i < lista.size(); i += tamañoBatch) {
      batches.add(lista.subList(i, Math.min(i + tamañoBatch, lista.size())));
    }
    return batches;
  }
}
