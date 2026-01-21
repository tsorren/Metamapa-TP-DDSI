package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class HechosCreadosService {

    private final FuenteDinamicaClient fuenteDinamicaClient;
    private final AgregadorClient agregadorClient;

    // Plazo de 7 días para poder modificar un hecho
    private static final long PLAZO_MODIFICACION_DIAS = 7;

    public HechosCreadosService(FuenteDinamicaClient fuenteDinamicaClient, AgregadorClient agregadorClient) {
        this.fuenteDinamicaClient = fuenteDinamicaClient;
        this.agregadorClient = agregadorClient;
    }

    /**
     * Obtener todos los hechos creados por un contribuyente
     */
    public List<HechoOutputDTO> obtenerHechosDeContribuyente(Long idContribuyente) {
        try {
            return fuenteDinamicaClient.obtenerHechosDeContribuyente(idContribuyente);
        } catch (Exception e) {
            log.error("Error al obtener hechos del contribuyente {}: {}", idContribuyente, e.getMessage());
            throw new RuntimeException("Error al obtener hechos del contribuyente", e);
        }
    }

    /**
     * Verificar si un hecho puede ser modificado (dentro de los primeros 7 días)
     */
    public boolean puedeModificar(HechoOutputDTO hecho) {
        if (hecho.getFechaDeCarga() == null) {
            return false;
        }
        
        LocalDateTime fechaLimite = hecho.getFechaDeCarga().plus(PLAZO_MODIFICACION_DIAS, ChronoUnit.DAYS);
        return LocalDateTime.now().isBefore(fechaLimite);
    }

    /**
     * Solicitar modificación de un hecho.
     * Compara los datos originales con los modificados y construye un DTO
     * con solo los campos que realmente cambiaron (los demás van en null).
     * Luego hace POST a /solicitudes-modificacion de FuenteDinamica.
     */
    public SolicitudDeModificacionOutputDTO solicitarModificacion(SolicitudModificacionFormDTO formData) {
        try {
            // Validar que haya al menos un campo modificado
            boolean hayModificaciones = false;
            
            String tituloModificado = null;
            if (!equals(formData.getTitulo(), formData.getTituloOriginal())) {
                tituloModificado = formData.getTitulo();
                hayModificaciones = true;
            }
            
            String descripcionModificada = null;
            if (!equals(formData.getDescripcion(), formData.getDescripcionOriginal())) {
                descripcionModificada = formData.getDescripcion();
                hayModificaciones = true;
            }
            
            Categoria categoriaModificada = null;
            if (!equals(formData.getCategoria(), formData.getCategoriaOriginal())) {
                categoriaModificada = formData.getCategoria();
                hayModificaciones = true;
            }
            
            Ubicacion ubicacionModificada = null;
            if (!equals(formData.getUbicacion(), formData.getUbicacionOriginal())) {
                ubicacionModificada = formData.getUbicacion();
                hayModificaciones = true;
            }
            
            LocalDate fechaModificada = null;
            if (!equals(formData.getFechaDelHecho(), formData.getFechaDelHechoOriginal())) {
                fechaModificada = formData.getFechaDelHecho();
                hayModificaciones = true;
            }
            
            Multimedia contenidoModificado = null;
            if (!equals(formData.getContenido(), formData.getContenidoOriginal())) {
                contenidoModificado = formData.getContenido();
                hayModificaciones = true;
            }
            
            if (!hayModificaciones) {
                throw new IllegalArgumentException("No se detectaron cambios en el hecho");
            }
            
            // Construir el DTO con solo los campos modificados
            HechoModificacionDTO modificacion = new HechoModificacionDTO(
                formData.getIdHecho(),
                formData.getIdContribuyente(),
                tituloModificado,
                descripcionModificada,
                categoriaModificada,
                ubicacionModificada,
                fechaModificada,
                contenidoModificado
            );
            
            log.info("Solicitando modificación del hecho {} con campos: titulo={}, descripcion={}, categoria={}, ubicacion={}, fecha={}, contenido={}",
                formData.getIdHecho(),
                tituloModificado != null,
                descripcionModificada != null,
                categoriaModificada != null,
                ubicacionModificada != null,
                fechaModificada != null,
                contenidoModificado != null
            );
            
            // Hacer POST a /solicitudes-modificacion
            return fuenteDinamicaClient.solicitarModificacionHecho(modificacion);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al solicitar modificación del hecho {}: {}", formData.getIdHecho(), e.getMessage());
            throw new RuntimeException("Error al solicitar modificación del hecho", e);
        }
    }
    
    /**
     * Comparar dos objetos con manejo de nulls
     */
    private boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null || o2 == null) return false;
        return o1.equals(o2);
    }

    /**
     * Solicitar eliminación de un hecho
     * IMPORTANTE: El idHecho que recibimos es el ID externo de FuenteDinamica.
     * Primero debemos obtener el ID interno de Agregador antes de solicitar la eliminación.
     */
    public SolicitudDeEliminacionOutputDTO solicitarEliminacion(SolicitudEliminacionInputDTO solicitud) {
        try {
            // Validar justificación mínima de 500 caracteres
            if (solicitud.getJustificacion() == null || solicitud.getJustificacion().trim().length() < 500) {
                throw new IllegalArgumentException("La justificación debe tener al menos 500 caracteres");
            }
            
            Long idExterno = solicitud.getIdHecho();
            log.info("Solicitando eliminación del hecho con ID externo (FuenteDinamica): {}", idExterno);

            // PASO 1: Obtener el hecho desde Agregador para conseguir el ID interno
            HechoOutputDTO hechoAgregador = agregadorClient.obtenerHechoDeFuenteDinamica(idExterno);
            Long idInterno = hechoAgregador.getIdentificador();
            
            log.info("ID externo (FuenteDinamica): {}, ID interno (Agregador): {}", idExterno, idInterno);
            
            // PASO 2: Crear solicitud con el ID interno de Agregador
            SolicitudEliminacionInputDTO solicitudConIdInterno = new SolicitudEliminacionInputDTO();
            solicitudConIdInterno.setIdHecho(idInterno); // Usar ID interno
            solicitudConIdInterno.setJustificacion(solicitud.getJustificacion());
            solicitudConIdInterno.setIdContribuyente(solicitud.getIdContribuyente());

            return agregadorClient.solicitarEliminacion(solicitudConIdInterno);
        } catch (Exception e) {
            log.error("Error al solicitar eliminación del hecho {}: {}", solicitud.getIdHecho(), e.getMessage());
            throw new RuntimeException("Error al solicitar eliminación del hecho", e);
        }
    }
}
