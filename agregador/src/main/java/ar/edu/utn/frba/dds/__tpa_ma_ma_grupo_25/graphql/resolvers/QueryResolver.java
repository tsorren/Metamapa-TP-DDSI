package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.graphql.resolvers;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechosFiltrandoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.NavegacionInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.*;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Resolver de GraphQL para todas las queries (consultas de lectura)
 * Define los métodos que resuelven las queries definidas en schema.graphqls
 */
@Slf4j
@Controller
public class QueryResolver {

    private final IHechoServicio hechoServicio;
    private final IColeccionServicio coleccionServicio;
    private final ICategoriaServicio categoriaServicio;
    private final IEtiquetaServicio etiquetaServicio;
    private final IFuenteServicio fuenteServicio;

    public QueryResolver(
            IHechoServicio hechoServicio,
            IColeccionServicio coleccionServicio,
            ICategoriaServicio categoriaServicio,
            IEtiquetaServicio etiquetaServicio,
            IFuenteServicio fuenteServicio
    ) {
        this.hechoServicio = hechoServicio;
        this.coleccionServicio = coleccionServicio;
        this.categoriaServicio = categoriaServicio;
        this.etiquetaServicio = etiquetaServicio;
        this.fuenteServicio = fuenteServicio;
    }

    /**
     * Query: hechos(filtro, paginacion)
     * Obtiene todos los hechos con filtros opcionales y paginación
     */
    @QueryMapping
    public Map<String, Object> hechos(
            @Argument Map<String, Object> filtro,
            @Argument Map<String, Object> paginacion
    ) {
        // Log comentado para producción - descomentar solo para debug
        // log.info("GraphQL Query: hechos - filtro: {}, paginacion: {}", filtro, paginacion);

        // Crear filtro con valores null (sin filtros aplicados)
        HechosFiltrandoInputDTO filtroDTO = new HechosFiltrandoInputDTO(
                null, // categoria
                null, // tipoOrigen
                null, // fecha_reporte_desde
                null, // fecha_reporte_hasta
                null, // fecha_acontecimiento_desde
                null, // fecha_acontecimiento_hasta
                null, // ubicacion
                null  // radio
        );

        List<HechoOutputDTO> hechos = hechoServicio.encontrarTodos(filtroDTO);

        // Aplicar paginación manual
        int pagina = paginacion != null ? (int) paginacion.getOrDefault("pagina", 0) : 0;
        int tamanio = paginacion != null ? (int) paginacion.getOrDefault("tamanio", 20) : 20;

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, hechos.size());
        List<HechoOutputDTO> paginado = inicio < hechos.size() ? hechos.subList(inicio, fin) : List.of();

        return Map.of(
                "contenido", paginado,
                "paginaActual", pagina,
                "totalPaginas", (int) Math.ceil((double) hechos.size() / tamanio),
                "totalElementos", (long) hechos.size(),
                "tamanio", tamanio
        );
    }

    /**
     * Query: hecho(id)
     * Obtiene un hecho específico por su ID
     */
    @QueryMapping
    public HechoOutputDTO hecho(@Argument String id) {
        // log.info("GraphQL Query: hecho - id: {}", id);
        return hechoServicio.encontrarPorId(Long.parseLong(id));
    }

    /**
     * Query: colecciones(filtro, paginacion)
     * Obtiene todas las colecciones con filtros opcionales
     */
    @QueryMapping
    public Map<String, Object> colecciones(
            @Argument Map<String, Object> filtro,
            @Argument Map<String, Object> paginacion
    ) {
        // log.info("GraphQL Query: colecciones - filtro: {}, paginacion: {}", filtro, paginacion);

        List<ColeccionOutputDTO> colecciones = coleccionServicio.encontrarTodas();

        // Aplicar paginación manual
        int pagina = paginacion != null ? (int) paginacion.getOrDefault("pagina", 0) : 0;
        int tamanio = paginacion != null ? (int) paginacion.getOrDefault("tamanio", 20) : 20;

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, colecciones.size());
        List<ColeccionOutputDTO> paginado = inicio < colecciones.size() ? colecciones.subList(inicio, fin) : List.of();

        return Map.of(
                "contenido", paginado,
                "paginaActual", pagina,
                "totalPaginas", (int) Math.ceil((double) colecciones.size() / tamanio),
                "totalElementos", (long) colecciones.size(),
                "tamanio", tamanio
        );
    }

    /**
     * Query: coleccion(handle)
     * Obtiene una colección específica por su handle
     */
    @QueryMapping
    public ConsultaColeccionOutputDTO coleccion(@Argument String handle) {
        // log.info("GraphQL Query: coleccion - handle: {}", handle);
        HechosFiltrandoInputDTO filtros = new HechosFiltrandoInputDTO(
                null, null, null, null, null, null, null, null
        );
        NavegacionInputDTO navegacion = new NavegacionInputDTO(handle, false, filtros);
        return coleccionServicio.consultarColeccion(navegacion);
    }

    /**
     * Query: hechosPorColeccion(handle, filtro, paginacion)
     * Obtiene los hechos de una colección específica
     */
    @QueryMapping
    public Map<String, Object> hechosPorColeccion(
            @Argument String handle,
            @Argument Map<String, Object> filtro,
            @Argument Map<String, Object> paginacion
    ) {
        // log.info("GraphQL Query: hechosPorColeccion - handle: {}, filtro: {}, paginacion: {}", handle, filtro, paginacion);

        HechosFiltrandoInputDTO filtros = new HechosFiltrandoInputDTO(
                null, null, null, null, null, null, null, null
        );
        NavegacionInputDTO navegacion = new NavegacionInputDTO(handle, false, filtros);
        ConsultaColeccionOutputDTO coleccionConsulta = coleccionServicio.consultarColeccion(navegacion);

        List<HechoOutputDTO> hechos = coleccionConsulta.getHechos();

        // Aplicar paginación
        int pagina = paginacion != null ? (int) paginacion.getOrDefault("pagina", 0) : 0;
        int tamanio = paginacion != null ? (int) paginacion.getOrDefault("tamanio", 20) : 20;

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, hechos.size());
        List<HechoOutputDTO> paginado = inicio < hechos.size() ? hechos.subList(inicio, fin) : List.of();

        return Map.of(
                "contenido", paginado,
                "paginaActual", pagina,
                "totalPaginas", (int) Math.ceil((double) hechos.size() / tamanio),
                "totalElementos", (long) hechos.size(),
                "tamanio", tamanio
        );
    }

    /**
     * Query: buscarHechos(texto, paginacion)
     * Busca hechos por texto en título o descripción
     */
    @QueryMapping
    public Map<String, Object> buscarHechos(
            @Argument String texto,
            @Argument Map<String, Object> paginacion
    ) {
        // log.info("GraphQL Query: buscarHechos - texto: {}, paginacion: {}", texto, paginacion);

        HechosFiltrandoInputDTO filtro = new HechosFiltrandoInputDTO(
                null, null, null, null, null, null, null, null
        );
        // El servicio actual no tiene búsqueda por texto, retornamos todos y filtramos
        List<HechoOutputDTO> todosLosHechos = hechoServicio.encontrarTodos(filtro);

        // Filtrar por texto en título o descripción
        List<HechoOutputDTO> hechosFiltrados = todosLosHechos.stream()
                .filter(h -> (h.getTitulo() != null && h.getTitulo().toLowerCase().contains(texto.toLowerCase())) ||
                        (h.getDescripcion() != null && h.getDescripcion().toLowerCase().contains(texto.toLowerCase())))
                .toList();

        // Aplicar paginación
        int pagina = paginacion != null ? (int) paginacion.getOrDefault("pagina", 0) : 0;
        int tamanio = paginacion != null ? (int) paginacion.getOrDefault("tamanio", 20) : 20;

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, hechosFiltrados.size());
        List<HechoOutputDTO> paginado = inicio < hechosFiltrados.size() ? hechosFiltrados.subList(inicio, fin) : List.of();

        return Map.of(
                "contenido", paginado,
                "paginaActual", pagina,
                "totalPaginas", (int) Math.ceil((double) hechosFiltrados.size() / tamanio),
                "totalElementos", (long) hechosFiltrados.size(),
                "tamanio", tamanio
        );
    }

    /**
     * Query: categorias()
     * Obtiene todas las categorías disponibles
     */
    @QueryMapping
    public List<CategoriaOutputDTO> categorias() {
        // log.info("GraphQL Query: categorias");
        return categoriaServicio.findAll();
    }

    /**
     * Query: etiquetas()
     * Obtiene todas las etiquetas disponibles
     */
    @QueryMapping
    public List<EtiquetaOutputDTO> etiquetas() {
        // log.info("GraphQL Query: etiquetas");
        return etiquetaServicio.encontrarTodas();
    }

    /**
     * Query: fuentes()
     * Obtiene todas las fuentes disponibles
     */
    @QueryMapping
    public List<FuenteOutputDTO> fuentes() {
        // log.info("GraphQL Query: fuentes");
        return fuenteServicio.encontrarTodasDTO();
    }
}

