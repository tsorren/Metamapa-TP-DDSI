package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoModificacionDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteDinamicaOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.FuenteDinamica;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteDinamicaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FuenteDinamicaServicioTest {

  private IFuenteDinamicaRepositorio fuenteRepositorio;
  private IHechoRepositorio hechoRepositorio;
  private IHechoServicio hechoServicio;
  private ISolicitudDeCreacionServicio solicitudServicio;

  private FuenteDinamicaServicio servicio;

  @BeforeEach
  void setUp() {
    fuenteRepositorio = mock(IFuenteRepositorioBD.class);
    hechoRepositorio = mock(IHechoRepositorioBD.class);
    hechoServicio = mock(IHechoServicio.class);
    solicitudServicio = mock(ISolicitudDeCreacionServicio.class);
    categoriaServicio = mock(ICategoriaServicio.class);
    servicio = new FuenteDinamicaServicio(fuenteRepositorio, hechoRepositorio, hechoServicio, solicitudServicio);
  }

  @Test
  void testCrearFuenteDinamica() {
    FuenteDinamicaOutputDTO dto = servicio.crear();
    assertNotNull(dto);
    verify(fuenteRepositorio, atLeastOnce()).guardar(any(FuenteDinamica.class));
  }

  @Test
  void testEncontrarTodas() {
    when(fuenteRepositorio.encontrarTodas()).thenReturn(List.of(new FuenteDinamica(), new FuenteDinamica()));
    List<FuenteDinamicaOutputDTO> resultado = servicio.encontrarTodas();
    assertEquals(2, resultado.size());
  }

  @Test
  void testHechosDeFuente() {
    when(fuenteRepositorio.idsDeHechosAsociados(1L)).thenReturn(List.of(10L));
    Hecho hecho = mock(Hecho.class);
    when(hecho.getVisible()).thenReturn(true);
    when(hechoRepositorio.encontrarPorId(10L)).thenReturn(hecho);
    when(hecho.getVisible()).thenReturn(true);
    when(hechoServicio.encontrarPorId(10L)).thenReturn(mock(HechoOutputDTO.class));

    List<HechoOutputDTO> hechos = servicio.hechosDeFuente(1L);
    assertEquals(1, hechos.size());
  }

  @Test
  void testHechoPorId_Pertenece() {
    when(fuenteRepositorio.idsDeHechosAsociados(1L)).thenReturn(List.of(10L));
    HechoOutputDTO dto = mock(HechoOutputDTO.class);
    when(hechoServicio.encontrarPorId(10L)).thenReturn(dto);
    assertEquals(dto, servicio.hechoPorId(1L, 10L));
  }

  @Test
  void testHechoPorId_NoPertenece() {
    when(fuenteRepositorio.idsDeHechosAsociados(1L)).thenReturn(List.of(11L));
    assertNull(servicio.hechoPorId(1L, 10L));
  }

  @Test
  void testCargarHecho() {
    Hecho hecho = mock(Hecho.class);
    when(hecho.getIdentificador()).thenReturn(999L);

    when(hechoServicio.crear(any(), any(), any(), any(), any(), any(), any(), eq(false)))
            .thenReturn(hecho);

    doAnswer(inv -> {
      Hecho hechoArg = inv.getArgument(0);
      // Simulamos que se guarda y tiene un ID
      when(hechoArg.getIdentificador()).thenReturn(999L);
      return null;
    }).when(hechoRepositorio).guardar(any());

    HechoOutputDTO dto = mock(HechoOutputDTO.class);
    when(solicitudServicio.crear(any(), any()))
            .thenReturn(mock(SolicitudDeCreacion.class));

    HechoOutputDTO resultado = servicio.cargarHecho(
            1L, "t", "d", 2L, 0.0, 0.0, LocalDate.now(), 3L
    );

    assertNotNull(resultado);
  }

  @Test
  void testModificarHecho_AutorizadoYNoVencido() {
    SolicitudDeCreacion solicitud = mock(SolicitudDeCreacion.class);
    when(solicitudServicio.encontrarPorIdHecho(100L)).thenReturn(solicitud);
    when(solicitudServicio.fueCreadaPor(solicitud, 1L)).thenReturn(true);
    when(solicitudServicio.estaVencida(solicitud)).thenReturn(false);
    when(hechoServicio.modificarHecho(any(HechoModificacionDTO.class))).thenReturn(mock(HechoOutputDTO.class));


    HechoModificacionDTO modificacion = new HechoModificacionDTO(100L, 1L, "t", "d", 2L, 1.1, 2.2, LocalDate.now(), 3L);
    HechoOutputDTO dto = servicio.modificarHecho(modificacion);
    verify(hechoServicio).encontrarPorId(100L);
    verify(solicitudServicio).encontrarPorIdHecho(100L);
    verify(solicitudServicio).fueCreadaPor(solicitud, 1L);
    verify(solicitudServicio).estaVencida(solicitud);
    verify(hechoServicio).modificarHecho(any(HechoModificacionDTO.class));
    verify(hechoRepositorio).guardar(any(Hecho.class));
    verify(solicitudServicio).crear(any(Hecho.class), eq(1L));
    assertNotNull(dto);
  }

  @Test
  void testModificarHecho_NoAutorizado() {
    SolicitudDeCreacion solicitud = mock(SolicitudDeCreacion.class);
    when(solicitudServicio.encontrarPorIdHecho(100L)).thenReturn(solicitud);
    when(solicitudServicio.fueCreadaPor(solicitud, 1L)).thenReturn(false);

    HechoModificacionDTO modificacion = new HechoModificacionDTO(100L, 1L, "t", "d", 2L, 1.1, 2.2, LocalDate.now(), 3L);
    HechoOutputDTO resultado = servicio.modificarHecho(modificacion);
    verify(solicitudServicio).encontrarPorIdHecho(100L);
    verify(solicitudServicio).fueCreadaPor(solicitud, 1L);
    verify(solicitudServicio, atLeastOnce()).estaVencida(solicitud);
    verify(hechoServicio, atLeastOnce()).modificarHecho(any(HechoModificacionDTO.class));
    assertNull(resultado);
  }
}
