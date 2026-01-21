package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Coleccion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.TipoConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso.AlgoritmoDeConsenso;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso.ConsensoAbsoluto;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso.ConsensoMayoriaSimple;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.planficadores.algoritmoConsenso.ConsensoMultiplesMenciones;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IColeccionRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.ColeccionServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion.FuenteServicio;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Data
@Component
public class ConsensoPlanificador {
  private final IColeccionRepositorioBD coleccionRepositorio;
  private final ColeccionServicio coleccionServicio;
  private final FuenteServicio fuenteServicio;
  private final Map<TipoConsenso, AlgoritmoDeConsenso> algoritmos;
  private LocalDateTime ultimoRefresco;


  public ConsensoPlanificador(
      FuenteServicio fuenteServicio,
      IColeccionRepositorioBD coleccionRepositorio,
      ColeccionServicio coleccionServicio
  ) {
    this.fuenteServicio = fuenteServicio;
    this.algoritmos = new EnumMap<>(TipoConsenso.class);
    algoritmos.put(TipoConsenso.MULTIPLE_MENCION, new ConsensoMultiplesMenciones(fuenteServicio));
    algoritmos.put(TipoConsenso.MAYORIA_SIMPLE, new ConsensoMayoriaSimple(fuenteServicio));
    algoritmos.put(TipoConsenso.ABSOLUTO, new ConsensoAbsoluto(fuenteServicio));
    this.coleccionRepositorio = coleccionRepositorio;
    this.coleccionServicio = coleccionServicio;
    this.ultimoRefresco = LocalDateTime.now();
  }

  @Transactional
  @Scheduled(cron = "${cron.intervaloCuracionHechos}")
  public void curarHechos() {
    log.info("<CURACION> Iniciando curacion de hechos");

    Map<TipoConsenso, Set<Hecho>> hechosPorTipoConsenso = new HashMap<>();
    List<Coleccion> colecciones = coleccionRepositorio.findAll();
    colecciones.forEach(c -> this.agregarHechosDeColeccion(c, hechosPorTipoConsenso));

    List<Fuente> fuentes = fuenteServicio.encontrarTodas();

    hechosPorTipoConsenso.forEach((tipoConsenso, hechos) -> {
      curarHechosDeTipo(tipoConsenso, hechos, fuentes);
      log.info("<CURACION> Hechos {} curados con algoritmo {}", hechos.size(), tipoConsenso);
    });

    this.ultimoRefresco = LocalDateTime.now();
    log.info("<CURACION> Curacion completada");
  }

  private void agregarHechosDeColeccion(Coleccion coleccion, Map<TipoConsenso, Set<Hecho>> hechosPorTipoConsenso) {
    TipoConsenso tipoConsenso = coleccion.getAlgoritmoConsenso();
    if (tipoConsenso == null) {
      return;
    }

    Set<Hecho> hechosDelTipo = hechosPorTipoConsenso.computeIfAbsent(
        tipoConsenso,
        k -> new HashSet<>()
    );

    List<Hecho> hechosDeColeccion = this.coleccionServicio.hechosDeColeccion(coleccion.getHandle());
    hechosDelTipo.addAll(hechosDeColeccion);
  }

  private boolean debeSerCurado(Hecho hecho, TipoConsenso tipoConsenso) {
    return !hecho.cumple(tipoConsenso);
  }

  private void curarHechosDeTipo(TipoConsenso tipoConsenso, Set<Hecho> hechos, List<Fuente> fuentes) {
    hechos.forEach(Hecho::quitarCurado); // TODO: SOLUCION TEMPORAL
    AlgoritmoDeConsenso algoritmo = algoritmos.get(tipoConsenso);

    hechos.stream()
        .filter(hecho -> debeSerCurado(hecho, tipoConsenso))
        .forEach(hecho -> algoritmo.curarHecho(hecho, fuentes));
  }
}
