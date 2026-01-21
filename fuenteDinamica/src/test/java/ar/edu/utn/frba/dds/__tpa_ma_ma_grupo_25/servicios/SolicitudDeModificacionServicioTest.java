package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.SolicitudDeCreacionOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.SolicitudDeModificacionServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SolicitudDeModificacionServicioTest {
  private ISolicitudDeModificacionRepositorio solicitudRepo;
  private IAdministradorRepositorio adminRepo;
  private IContribuyenteRepositorio contribRepo;
  private IFuenteDinamicaRepositorio fuenteRepo;
  private SolicitudDeModificacionServicio servicio;
  private IHechoServicio hechoServicio;
  private IHechoRepositorio hechoRepositorio;

  @BeforeEach
  void setUp() {
    solicitudRepo = mock(ISolicitudDeModificacionRepositorio.class);
    adminRepo = mock(IAdministradorRepositorio.class);
    contribRepo = mock(IContribuyenteRepositorio.class);
    fuenteRepo = mock(IFuenteDinamicaRepositorio.class);
    hechoServicio = mock(IHechoServicio.class);
    hechoRepositorio = mock(IHechoRepositorio.class);

    servicio = new SolicitudDeModificacionServicio(
            solicitudRepo, adminRepo, contribRepo, fuenteRepo, 3, hechoRepositorio, hechoServicio
    );
  }

  @Test
  void testCrear() {
    Hecho hecho = mock(Hecho.class);
    Contribuyente contribuyente = mock(Contribuyente.class);
    when(contribRepo.encontrarPorId(1L)).thenReturn(contribuyente);

    SolicitudDeCreacion solicitud = servicio.crear(hecho, 1L);

    verify(solicitudRepo).guardar(solicitud);
    verify(solicitudRepo).guardarEstado(solicitud.estadoActual());
    assertEquals(hecho, solicitud.getHecho());
  }

  @Test
  void testEncontrarTodas() {
    when(solicitudRepo.encontrarTodas(null)).thenReturn(List.of(mock(SolicitudDeCreacion.class)));
    List<SolicitudDeCreacionOutputDTO> lista = servicio.encontrarTodas(null);
    assertEquals(1, lista.size());
  }

  @Test
  void testProcesar() {
    SolicitudDeCreacion solicitud = mock(SolicitudDeCreacion.class);
    Hecho hecho = mock(Hecho.class);
    Administrador admin = mock(Administrador.class);

    when(solicitud.getHecho()).thenReturn(hecho);
    when(solicitud.getIdentificador()).thenReturn(1L);
    when(solicitudRepo.encontrarPorId(1L)).thenReturn(solicitud);
    when(adminRepo.encontrarPorId(1L)).thenReturn(admin);

    SolicitudDeCreacionOutputDTO dto = servicio.procesar(1L, 1L, TipoEstadoRevision.ACEPTADA, "ok");

    verify(solicitud).procesar(admin, TipoEstadoRevision.ACEPTADA, "ok");
    verify(solicitudRepo).guardarEstado(solicitud.estadoActual());
    verify(fuenteRepo).cargarHecho(1L, hecho.getIdentificador());
    assertNotNull(dto);
  }

  @Test
  void testEstaVencida() {
    SolicitudDeCreacion solicitud = mock(SolicitudDeCreacion.class);
    when(solicitud.ultimaFechaProcesamiento()).thenReturn(LocalDateTime.now().minusDays(5));
    assertTrue(servicio.estaVencida(solicitud));
  }

  @Test
  void testFueCreadaPor() {
    Contribuyente contribuyente = mock(Contribuyente.class);
    when(contribuyente.getIdentificador()).thenReturn(99L);
    SolicitudDeCreacion solicitud = mock(SolicitudDeCreacion.class);
    when(solicitud.getContribuyente()).thenReturn(contribuyente);
    assertTrue(servicio.fueCreadaPor(solicitud, 99L));
  }
}
