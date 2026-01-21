package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.repositorios;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.EstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.SolicitudDeCreacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.solicitud.TipoEstadoRevision;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.implementacion.SolicitudDeCreacionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public @DisplayName("Tests de SolicitudDeCreacionRepositorio")
class SolicitudDeCreacionRepositorioTest {
  private SolicitudDeCreacionRepositorio repositorio;

  @BeforeEach
  void setUp() {
    repositorio = new SolicitudDeCreacionRepositorio();
  }

  @Test
  @DisplayName("Guarda solicitud con ID e indexa por hecho")
  void guardaSolicitud() {
    Hecho hecho = Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL);
    hecho.setIdentificador(1L);
    Contribuyente c = Contribuyente.crear("Carlos");
    SolicitudDeCreacion s = new SolicitudDeCreacion(hecho, c);
    repositorio.guardar(s);
    assertNotNull(s.getIdentificador());
    assertEquals(s, repositorio.encontrarPorIdHecho(1L));
  }

  @Test
  @DisplayName("Filtra solicitudes por estado")
  void encontrarTodasPorEstado() {
    SolicitudDeCreacion s1 = new SolicitudDeCreacion(Hecho.crear("Corte de luz", "Apagón masivo", new Categoria("Infraestructura"),
            new Ubicacion(-33.4489, -70.6693), LocalDate.of(2024, 1, 15), TipoOrigen.MANUAL), Contribuyente.crear("A"));
    s1.getHecho().setIdentificador(10L);
    repositorio.guardar(s1);
    assertEquals(1, repositorio.encontrarTodas(TipoEstadoRevision.PENDIENTE).size());
  }

  @Test
  @DisplayName("Guarda y recupera estado de revisión")
  void guardarEstadoRevision() {
    EstadoRevision estado = new EstadoRevision(TipoEstadoRevision.RECHAZADA, "No válido");
    repositorio.guardarEstado(estado);
    assertNotNull(estado.getIdentificador());
    assertEquals(estado, repositorio.encontrarPorIdEstado(estado.getIdentificador()));
  }
}
