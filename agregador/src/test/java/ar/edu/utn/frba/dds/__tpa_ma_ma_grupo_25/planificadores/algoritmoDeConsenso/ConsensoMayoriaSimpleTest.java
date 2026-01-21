package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planificadores.algoritmoDeConsenso;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso.ConsensoMayoriaSimple;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsensoMayoriaSimpleTest {

  private FuenteServicio fuenteServicio;
  private ConsensoMayoriaSimple consenso;

  @BeforeEach
  void setUp() {
    fuenteServicio = Mockito.mock(FuenteServicio.class);
    consenso = new ConsensoMayoriaSimple(fuenteServicio);
  }

  @Test
  void cumpleCriterioCurado_CuandoLaMitadDeLasFuentesContienenElMismoHecho() {
    Fuente fuente1 = Mockito.mock(Fuente.class);
    Fuente fuente2 = Mockito.mock(Fuente.class);
    Fuente fuente3 = Mockito.mock(Fuente.class);
    Fuente fuente4 = Mockito.mock(Fuente.class);

    Hecho hechoOriginal = Mockito.mock(Hecho.class);
    Hecho igual1 = Mockito.mock(Hecho.class);
    Hecho igual2 = Mockito.mock(Hecho.class);
    Hecho distinto = Mockito.mock(Hecho.class);

    Mockito.when(hechoOriginal.tieneMismosAtributos(igual1)).thenReturn(true);
    Mockito.when(hechoOriginal.tieneMismosAtributos(igual2)).thenReturn(true);
    Mockito.when(hechoOriginal.tieneMismosAtributos(distinto)).thenReturn(false);

    boolean resultado = consenso.cumpleCriterioCurado(
            hechoOriginal,
            List.of(igual1, igual2, distinto),
            List.of(fuente1, fuente2, fuente3, fuente4)
    );

    assertTrue(resultado); // 2 iguales de 4 fuentes => mayoría simple (2 >= 2)
  }
}
