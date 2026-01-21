package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.pruebas;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.Filtro;


public class CriteriosTest {
  private FuenteEstatica fuenteEstatica;
  private CriterioDePertenencia criterio;
  private Coleccion coleccion;
  private Filtro filtro;
/*
    @BeforeEach
    void init() {
        Path ruta = Paths.get("datos/desastres_naturales_argentina.csv");
        fuenteEstatica = new FuenteEstatica(ruta);
        fuenteEstatica.importarHechos();
        criterio = new CriterioDePertenencia(); //Sin criterios
        coleccion = new Coleccion("Colección prueba", "Esto es una prueba", fuenteEstatica, criterio);
    }

    @Test
    @DisplayName("Hechos con Categoría")
    public void CategoriaTest() {
        filtro = new FiltroCategoria(fuenteEstatica.getHechosFuente().get(0).getCategoria());
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos con Descripcion")
    public void DescripcionTest() {
        filtro = new FiltroDescripcion(fuenteEstatica.getHechosFuente().get(0).getDescripcion());
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos con Fecha Entre: 2001-01-01 y 2001-02-01")
    public void FechaEntreTest() {
        filtro = new FiltroFechaEntre(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 1, 7));
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos con Fecha Exacta: 2010-01-01")
    public void FechaExactaTest() {
        filtro = new FiltroFechaExacta(LocalDate.of(2010, 1, 1));
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos con Titulo")
    public void TituloTest() {
        filtro = new FiltroTitulo(fuenteEstatica.getHechosFuente().get(0).getTitulo());
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos a 2.5km de Ubicacion")
    public void UbicacionTest() {
        filtro = new FiltroUbicacion(fuenteEstatica.getHechosFuente().get(0).getUbicacion(), 2.5f);
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }


    @Test
    @DisplayName("Hechos con TipoOrigen")
    public void TipoOrigenTest() {
        filtro = new FiltroTipoOrigen(fuenteEstatica.getHechosFuente().get(0).getOrigen());
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    @Test
    @DisplayName("Hechos con Contenido Texto")
    public void MultimediaTest() {
        filtro = new FiltroMultimedia(TipoContenido.TEXTO);
        criterio.agregarFiltro(filtro);
        Assertions.assertEquals(coleccion.consultarColeccion(), criterio.aplicar(fuenteEstatica.getHechosFuente()));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
    }

    private void mostrarHecho(Hecho hecho) {
        System.out.println("---------- HECHO ----------");
        System.out.println("Título: " + hecho.getTitulo());
        System.out.println("Descripción: " + hecho.getDescripcion());
        System.out.println("Categoría: " + hecho.getCategoria().getNombre());
        System.out.println("Fecha del hecho: " + hecho.getFechaDelHecho());
        System.out.println("Fecha de carga: " + hecho.getFechaDeCarga());
        System.out.println("Ubicación: (" + hecho.getUbicacion().getLatitud() + ", " + hecho.getUbicacion().getLongitud() + ")");
        System.out.println("Origen: " + hecho.getOrigen());
        System.out.println("Visible: " + hecho.getVisible());
        System.out.println("Etiquetas: " + hecho.getEtiquetas());
        System.out.println("Multimedia: " + hecho.getContenido());
    }

 */
}
