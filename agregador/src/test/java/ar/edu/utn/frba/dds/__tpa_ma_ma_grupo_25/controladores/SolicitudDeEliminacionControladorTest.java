package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.administrador.AdminSolicitudDeEliminacionControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica.PublicSolicitudDeEliminacionControlador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ProcesamientoSolicitudInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.SolicitudInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoAtencionSolicitud;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISolicitudEliminacionServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class SolicitudDeEliminacionControladorTest {

  private ISolicitudEliminacionServicio servicioMock;
  private PublicSolicitudDeEliminacionControlador controladorPublic;
  private AdminSolicitudDeEliminacionControlador controladorAdmin;

  @BeforeEach
  public void setup() {
    servicioMock = mock(ISolicitudEliminacionServicio.class);
    controladorPublic = new PublicSolicitudDeEliminacionControlador(servicioMock);
  }

  @Test
  public void testCrearSolicitud() {
    SolicitudInputDTO input = new SolicitudInputDTO();
    input.setIdHecho(10L);
    input.setJustificacion("Justificacion test");
    input.setIdContribuyente(5L);

    SolicitudOutputDTO outputDTO = mock(SolicitudOutputDTO.class);
    when(servicioMock.solicitarEliminacion(10L, "Justificacion test", 5L)).thenReturn(outputDTO);

    SolicitudOutputDTO resultado = controladorPublic.crear(input);

    verify(servicioMock).solicitarEliminacion(10L, "Justificacion test", 5L);
    assertNotNull(resultado);
    assertEquals(outputDTO, resultado);
  }

  @Test
  public void testEncontrarTodasSinEstado() {
    List<SolicitudOutputDTO> listaMock = List.of(mock(SolicitudOutputDTO.class), mock(SolicitudOutputDTO.class));
    when(servicioMock.encontrarTodas(null)).thenReturn(listaMock);

    List<SolicitudOutputDTO> resultado = controladorPublic.encontrarTodas(null);

    verify(servicioMock).encontrarTodas(null);
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
  }

  @Test
  public void testEncontrarTodasConEstado() {
    EstadoAtencionSolicitud estado = EstadoAtencionSolicitud.PENDIENTE;
    List<SolicitudOutputDTO> listaMock = List.of(mock(SolicitudOutputDTO.class));
    when(servicioMock.encontrarTodas(estado)).thenReturn(listaMock);

    List<SolicitudOutputDTO> resultado = controladorPublic.encontrarTodas(estado);

    verify(servicioMock).encontrarTodas(estado);
    assertNotNull(resultado);
    assertEquals(1, resultado.size());
  }

  @Test
  public void testProcesarSolicitud() {
    Long idSolicitud = 1L;

    ProcesamientoSolicitudInputDTO input = new ProcesamientoSolicitudInputDTO();
    input.setIdResponsable(2L);
    input.setEstado(EstadoAtencionSolicitud.ACEPTADA);
    input.setJustificacion("Procesado correctamente");

    SolicitudOutputDTO outputDTO = mock(SolicitudOutputDTO.class);
    when(servicioMock.procesarSolicitud(idSolicitud, 2L, EstadoAtencionSolicitud.ACEPTADA, "Procesado correctamente"))
            .thenReturn(outputDTO);

    SolicitudOutputDTO resultado = controladorAdmin.procesarSolicitud(idSolicitud, input);

    verify(servicioMock).procesarSolicitud(idSolicitud, 2L, EstadoAtencionSolicitud.ACEPTADA, "Procesado correctamente");
    assertNotNull(resultado);
    assertEquals(outputDTO, resultado);
  }
}

