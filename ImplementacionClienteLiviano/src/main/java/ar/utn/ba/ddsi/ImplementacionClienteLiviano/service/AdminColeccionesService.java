package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdminColeccionesService {

  private final AgregadorClient agregadorClient;

  public AdminColeccionesService(AgregadorClient agregadorClient) {
    this.agregadorClient = agregadorClient;
  }

  /**
   * Crear una colección con criterios de pertenencia dinámicos.
   * Flujo:
   * 1. Crear criterio vacío
   * 2. Por cada filtro: crear filtro y agregarlo al criterio
   * 3. Crear la colección con el idCriterio resultante
   */
  public ColeccionDTO crearColeccionConFiltros(CrearColeccionConFiltrosDTO datos) {
    try {
      // PASO 1: Crear criterio de pertenencia vacío
      log.info("Creando criterio de pertenencia vacío...");
      CriterioDePertenenciaOutputDTO criterio = agregadorClient.crearCriterioDePertenencia();
      Long idCriterio = criterio.getIdentificador();
      log.info("Criterio creado con ID: {}", idCriterio);

      // PASO 2: Crear filtros y agregarlos al criterio
      List<FiltroInputDTO> filtros = datos.getFiltros();
      if (filtros != null && !filtros.isEmpty()) {
        log.info("Procesando {} filtros...", filtros.size());
        
        for (FiltroInputDTO filtroInput : filtros) {
          // Crear el filtro
          log.info("Creando filtro de tipo: {}", filtroInput.getTipoFiltro());
          FiltroOutputDTO filtroCreado = agregadorClient.crearFiltro(filtroInput);
          Long idFiltro = filtroCreado.getIdentificador();
          log.info("Filtro creado con ID: {}", idFiltro);

          // Agregar el filtro al criterio
          log.info("Agregando filtro {} al criterio {}...", idFiltro, idCriterio);
          agregadorClient.agregarFiltroACriterio(idCriterio, idFiltro);
          log.info("Filtro {} agregado exitosamente al criterio {}", idFiltro, idCriterio);
        }
      } else {
        log.info("No hay filtros para agregar, criterio vacío");
      }

      // PASO 3: Crear la colección usando el criterio
      log.info("Creando colección con criterio {}...", idCriterio);
      ColeccionInputDTO coleccionInput = new ColeccionInputDTO();
      coleccionInput.setTitulo(datos.getTitulo());
      coleccionInput.setDescripcion(datos.getDescripcion());
      coleccionInput.setCriterioDePertenenciaID(idCriterio);
      coleccionInput.setIdsFuentes(datos.getIdsFuentes());
      coleccionInput.setAlgoritmoConsenso(datos.getAlgoritmoConsenso());

      ColeccionDTO coleccionCreada = agregadorClient.crearColeccion(coleccionInput);
      log.info("Colección creada exitosamente: {}", coleccionCreada.getTitulo());

      return coleccionCreada;

    } catch (Exception e) {
      log.error("Error al crear colección con filtros: {}", e.getMessage(), e);
      throw new RuntimeException("Error al crear colección con filtros: " + e.getMessage(), e);
    }
  }

  /**
   * Actualizar una colección existente con nuevos filtros.
   * Flujo:
   * 1. Obtener la colección actual para conocer el idCriterio
   * 2. Obtener los filtros actuales del criterio
   * 3. Quitar todos los filtros existentes del criterio
   * 4. Crear nuevos filtros y agregarlos al criterio
   * 5. Actualizar la colección con los demás datos
   */
  public ColeccionDTO actualizarColeccionConFiltros(String handle, ActualizarColeccionConFiltrosDTO datos) {
    try {
      // PASO 1: Obtener la colección actual
      //log.info("Obteniendo colección con handle: {}...", handle);
      ColeccionDTO coleccionActual = agregadorClient.obtenerColeccionPorHandle(handle);
      if (coleccionActual == null) {
        throw new RuntimeException("Colección no encontrada con handle: " + handle);
      }

      Long idCriterio = coleccionActual.getCriterioDePertenencia() != null
          ? coleccionActual.getCriterioDePertenencia().getIdentificador()
          : null;

      if (idCriterio == null) {
        // Si no tiene criterio, crear uno nuevo
        //log.info("La colección no tiene criterio de pertenencia, creando uno nuevo...");
        CriterioDePertenenciaOutputDTO nuevoCriterio = agregadorClient.crearCriterioDePertenencia();
        idCriterio = nuevoCriterio.getIdentificador();
        //log.info("Nuevo criterio creado con ID: {}", idCriterio);
      } else {
        // PASO 2 y 3: Obtener y quitar filtros existentes
        //log.info("Obteniendo criterio de pertenencia actual con ID: {}...", idCriterio);
        CriterioDePertenenciaOutputDTO criterioActual = agregadorClient.obtenerCriterioDePertenencia(idCriterio);

        if (criterioActual.getFiltros() != null && !criterioActual.getFiltros().isEmpty()) {
          //log.info("Quitando {} filtros existentes del criterio...", criterioActual.getFiltros().size());
          for (FiltroOutputDTO filtro : criterioActual.getFiltros()) {
            //log.info("Quitando filtro ID: {} del criterio {}", filtro.getIdentificador(), idCriterio);
            agregadorClient.quitarFiltroDeCriterio(idCriterio, filtro.getIdentificador());
          }
          //log.info("Filtros existentes removidos exitosamente");
        }
      }

      // PASO 4: Crear nuevos filtros y agregarlos al criterio
      List<FiltroInputDTO> nuevosFiltros = datos.getFiltros();
      if (nuevosFiltros != null && !nuevosFiltros.isEmpty()) {
        //log.info("Procesando {} nuevos filtros...", nuevosFiltros.size());

        for (FiltroInputDTO filtroInput : nuevosFiltros) {
          //log.info("Creando filtro de tipo: {}", filtroInput.getTipoFiltro());
          FiltroOutputDTO filtroCreado = agregadorClient.crearFiltro(filtroInput);
          Long idFiltro = filtroCreado.getIdentificador();
          //log.info("Filtro creado con ID: {}", idFiltro);

          //log.info("Agregando filtro {} al criterio {}...", idFiltro, idCriterio);
          agregadorClient.agregarFiltroACriterio(idCriterio, idFiltro);
          //log.info("Filtro {} agregado exitosamente al criterio {}", idFiltro, idCriterio);
        }
      }

      // PASO 5: Actualizar la colección con los demás datos
      //log.info("Actualizando colección con handle {}...", handle);
      ColeccionInputDTO coleccionInput = new ColeccionInputDTO();
      coleccionInput.setTitulo(datos.getTitulo());
      coleccionInput.setDescripcion(datos.getDescripcion());
      coleccionInput.setCriterioDePertenenciaID(idCriterio);
      coleccionInput.setIdsFuentes(datos.getIdsFuentes());
      coleccionInput.setAlgoritmoConsenso(datos.getAlgoritmoConsenso());

      ColeccionDTO coleccionActualizada = agregadorClient.actualizarColeccion(handle, coleccionInput);
      log.info("Colección actualizada exitosamente: {}", coleccionActualizada.getTitulo());

      return coleccionActualizada;

    } catch (Exception e) {
      log.error("Error al actualizar colección con filtros: {}", e.getMessage(), e);
      throw new RuntimeException("Error al actualizar colección con filtros: " + e.getMessage(), e);
    }
  }
}
