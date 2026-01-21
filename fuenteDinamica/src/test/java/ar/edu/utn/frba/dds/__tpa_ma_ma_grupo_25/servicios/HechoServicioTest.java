package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

class HechoServicioTest {

/*  private IHechoRepositorioBD hechoRepositorio;
  private ICategoriaRepositorioBD categoriaRepositorio;
  private IContenidoRepositorioBD contenidoRepositorio;
  private HechoServicio hechoServicio;

  @BeforeEach
  void setUp() {
    hechoRepositorio = mock(IHechoRepositorioBD.class);
    categoriaRepositorio = mock(ICategoriaRepositorioBD.class);
    contenidoRepositorio = mock(IContenidoRepositorioBD.class);
    //hechoServicio = new HechoServicio(hechoRepositorio, categoriaRepositorio, contenidoRepositorio);
  }

  @Test
  void testCrearHecho() {
    // Arrange
    String titulo = "Incendio";
    String descripcion = "Fuerte incendio en zona norte";
    Long idCategoria = 1L;
    Double lat = -34.6037;
    Double lon = -58.3816;
    LocalDate fecha = LocalDate.now();
    Long idContenido = 5L;
    Boolean visible = true;

    Categoria categoria = new Categoria("Incendios");
    Multimedia contenido = new Multimedia(1L, TipoContenido.IMAGEN, "htttp//googleimages.com");
    when(categoriaRepositorio.findById(idCategoria)).thenReturn(categoria);
    when(contenidoRepositorio.findById(idContenido)).thenReturn(contenido);

    // Act
    Hecho hecho = hechoServicio.crear(titulo, descripcion, idCategoria, lat, lon, fecha, idContenido, visible);

    // Assert
    assertEquals(titulo, hecho.getTitulo());
    assertEquals(descripcion, hecho.getDescripcion());
    assertEquals(categoria, hecho.getCategoria());
    assertEquals(lat, hecho.getUbicacion().getLatitud());
    assertEquals(lon, hecho.getUbicacion().getLongitud());
    assertEquals(fecha, hecho.getFechaDelHecho());
    assertEquals(TipoOrigen.CONTRIBUYENTE, hecho.getOrigen());
    assertEquals(visible, hecho.getVisible());
    assertEquals(lat, hecho.getUbicacion().getLatitud());
    assertEquals(lon, hecho.getUbicacion().getLongitud());
    //verify(ubicacionRepositorio, times(1)).guardar(any(Ubicacion.class));
  }

  @Test
  void testEncontrarTodos() {
    // Arrange
    Hecho h = Hecho.crear("Titulo", "Desc", new Categoria("C"), new Ubicacion(0.0, 0.0), LocalDate.now(), TipoOrigen.CONTRIBUYENTE, new Multimedia(1L, TipoContenido.IMAGEN, "url"), true);
    when(hechoRepositorio.findAll()).thenReturn(List.of(h));

    // Act
    var lista = hechoServicio.encontrarTodos();

    // Assert
    assertEquals(1, lista.size());
  }

  @Test
  void testEncontrarPorId() {
    // Arrange
    Long id = 1L;
    Hecho hecho = Hecho.crear("T", "D", new Categoria("Cat"), new Ubicacion(0.0, 0.0), LocalDate.now(), TipoOrigen.CONTRIBUYENTE, new Multimedia(1L, TipoContenido.AUDIO, "url"), true);
    when(hechoRepositorio.findById(id)).thenReturn(hecho);

    // Act
    var dto = hechoServicio.encontrarPorId(id);

    // Assert
    assertEquals("T", dto.getTitulo());
    assertEquals("D", dto.getDescripcion());
  }

  @Test
  void testOcultarHecho() {
    // Arrange
    Long id = 1L;
    Hecho hecho = mock(Hecho.class);
    when(hechoRepositorio.findById(id)).thenReturn(hecho);

    // Act
    hechoServicio.ocultarHecho(id);

    // Assert
    verify(hecho, times(1)).setVisible(false);
  }*/
}
