package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.escenarios;


import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;


public class Escenario3Test {
  private Administrador administrador;
  private Contribuyente contribuyente;
  private Hecho hecho;
  private SolicitudDeEliminacion solicitud;
/*
    @BeforeEach
    void init() {
        administrador = new Administrador(0);
        contribuyente = new Contribuyente("pepe");

        // Hecho 1
        hecho = Hecho.crear(
                "Caída de aeronave impacta en Olavarría",
                "Grave caída de aeronave ocurrió en las inmediaciones de Olavarría, Buenos Aires. El incidente provocó pánico entre los residentes locales. Voluntarios de diversas organizaciones se han sumado a las tareas de auxilio.",
                new Categoria("Caída de aeronave"),
                new Ubicacion(-36.868375, -60.343297),
                LocalDate.of(2001, 11, 29),
                TipoOrigen.MANUAL
        );

        solicitud = new SolicitudDeEliminacion(hecho, "una justificacion ", contribuyente);
    }

    @Test
    void administradorAceptaSolicitud() {
        solicitud.aceptar(administrador);
        Assertions.assertEquals(EstadoAtencionSolicitud.ACEPTADA, solicitud.getEstadoAtencion());
        Assertions.assertFalse(hecho.getVisible());
    }

    @Test
    void administradorRechazarSolicitud() {
        solicitud.rechazar(administrador);
        Assertions.assertEquals(EstadoAtencionSolicitud.RECHAZADA, solicitud.getEstadoAtencion());
        Assertions.assertTrue(hecho.getVisible());
    }

 */
}
