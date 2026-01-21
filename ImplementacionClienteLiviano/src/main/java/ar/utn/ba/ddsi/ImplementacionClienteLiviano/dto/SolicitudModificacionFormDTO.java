package ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para recibir todos los datos del formulario de modificación.
 * Contiene tanto datos originales como modificados para que el backend pueda compararlos.
 */
@Data
public class SolicitudModificacionFormDTO {
    private Long idHecho;
    private Long idContribuyente;
    
    // Datos originales
    private String tituloOriginal;
    private String descripcionOriginal;
    private Categoria categoriaOriginal;
    private Ubicacion ubicacionOriginal;
    private LocalDate fechaDelHechoOriginal;
    private Multimedia contenidoOriginal;
    
    // Datos modificados del formulario
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private Ubicacion ubicacion;
    private LocalDate fechaDelHecho;
    private Multimedia contenido;
}
