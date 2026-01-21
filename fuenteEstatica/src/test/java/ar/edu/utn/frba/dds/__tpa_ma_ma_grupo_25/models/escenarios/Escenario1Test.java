package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.escenarios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteEstatica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.criterios.CriterioDePertenencia;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Escenario1Test {
  private final Path ruta = Paths.get("datos/Escenario_1.csv");
  FuenteEstatica fuenteEstatica;
  private CriterioDePertenencia criterio;
  private Coleccion coleccion;
/*
    @BeforeEach
    public void init() {
        criterio = new CriterioDePertenencia();
        fuenteEstatica = new FuenteEstatica(ruta);



        // Hecho 1
        fuenteEstatica.agregarHecho(
                Hecho.crear(
                        "Caída de aeronave impacta en Olavarría",
                        "Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
                        new Categoria("Caída de aeronave"),
                        new Ubicacion(-36.868375, -60.343297),
                        LocalDate.of(2001, 11, 29),
                        TipoOrigen.MANUAL
                )
        );

        // Hecho 2
        fuenteEstatica.agregarHecho(
                Hecho.crear(
                        "Serio incidente: Accidente con maquinaria industrial en Chos Malal, Neuquén",
                        "Un grave accidente con maquinaria industrial se registró en Chos Malal, Neuquén. El incidente dejó a varios sectores sin comunicación. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
                        new Categoria("Accidente con maquinaria industrial"),
                        new Ubicacion(-37.345571, -70.241485),
                        LocalDate.of(2001, 8, 16),
                        TipoOrigen.MANUAL
                )
        );

        // Hecho 3
        fuenteEstatica.agregarHecho(
                Hecho.crear(
                        "Caída de aeronave impacta en Venado Tuerto, Santa Fe",
                        "Grave caída de aeronave ocurrió en las inmediaciones de Venado Tuerto, Santa Fe. El incidente destruyó viviendas y dejó a familias evacuadas. Autoridades nacionales se han puesto a disposición para brindar asistencia.",
                        new Categoria("Caída de aeronave"),
                        new Ubicacion(-33.768051, -61.921032),
                        LocalDate.of(2008, 8, 8),
                        TipoOrigen.MANUAL
                )
        );

        // Hecho 4
        fuenteEstatica.agregarHecho(
                Hecho.crear(
                        "Accidente en paso a nivel deja múltiples daños en Pehuajó, Buenos Aires",
                        "Grave accidente en paso a nivel ocurrió en las inmediaciones de Pehuajó, Buenos Aires. El incidente generó preocupación entre las autoridades provinciales. El Ministerio de Desarrollo Social está brindando apoyo a los damnificados.",
                        new Categoria("Accidente en paso a nivel"),
                        new Ubicacion(-35.855811, -61.940589),
                        LocalDate.of(2020, 1, 27),
                        TipoOrigen.MANUAL
                )
        );

        // Hecho 5
        fuenteEstatica.agregarHecho(
                Hecho.crear(
                        "Devastador Derrumbe en obra en construcción afecta a Presidencia Roque Sáenz Peña",
                        "Un grave derrumbe en obra en construcción se registró en Presidencia Roque Sáenz Peña, Chaco. El incidente generó preocupación entre las autoridades provinciales. El intendente local se ha trasladado al lugar para supervisar las operaciones.",
                        new Categoria("Derrumbe en obra en construcción"),
                        new Ubicacion(-26.780008, -60.458782),
                        LocalDate.of(2016, 6, 4),
                        TipoOrigen.MANUAL
                )
        );

        // Escenario 1.1
        coleccion = new Coleccion(
                "Colección prueba",
                "Esto es una prueba",
                fuenteEstatica,
                criterio
        );
    }

    // Escenario 1.1
    @Test
    public void PrimerTest() {
        coleccion.consultarColeccion().forEach(this::mostrarHecho);
        Assertions.assertEquals(fuenteEstatica.getHechosFuente(), coleccion.consultarColeccion());
    }

    // Escenario 1.2
    @Test
    public void SegundoTest() {
        List<Hecho> hechosFuente = fuenteEstatica.getHechosFuente();
        criterio.agregarFiltro(new FiltroFechaEntre(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 1, 1)
        ));
        coleccion.consultarColeccion().forEach(this::mostrarHecho);
        Assertions.assertEquals(List.of(hechosFuente.get(0), hechosFuente.get(1), hechosFuente.get(2)), coleccion.consultarColeccion());
    }

    @Test
    public void TercerTest() {
        List<Hecho> hechosFuente = fuenteEstatica.getHechosFuente();
        criterio.agregarFiltro(new FiltroFechaEntre(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 1, 1)
        ));
        coleccion.consultarColeccion().forEach(this::mostrarHecho);
        criterio.agregarFiltro(new FiltroCategoria(new Categoria("Caída de aeronave")));

        coleccion.consultarColeccion().forEach(this::mostrarHecho);
        Assertions.assertEquals(List.of(hechosFuente.get(0), hechosFuente.get(2)), coleccion.consultarColeccion());
    }

    // Escenario 1.3
    @Test
    public void CuartoTest() {
        CriterioDePertenencia filtros = new CriterioDePertenencia();
        filtros.agregarFiltro(new FiltroCategoria(new Categoria("Caída de Aeronave")));
        filtros.agregarFiltro(new FiltroTitulo("un titulo"));
        Assertions.assertTrue(coleccion.filtarHechos(filtros).isEmpty());

    }

    // Escenario 1.4
    @Test
    public void QuintoTest() {
        criterio.agregarFiltro(new FiltroFechaEntre(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 1, 1)
        ));
        criterio.agregarFiltro(new FiltroFechaEntre(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2010, 1, 1)
        ));
        Hecho hechoEtiquetado = fuenteEstatica.getHechosFuente().get(0);

        Etiqueta e1 = new Etiqueta("Olavarría");
        Etiqueta e2 = new Etiqueta("Grave");
        hechoEtiquetado.agregarEtiqueta(e1);
        hechoEtiquetado.agregarEtiqueta(e2);
        Assertions.assertEquals(Set.of(e1, e2), hechoEtiquetado.getEtiquetas());
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