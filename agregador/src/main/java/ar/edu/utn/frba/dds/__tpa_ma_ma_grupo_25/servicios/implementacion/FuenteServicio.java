package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.FuenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.HechoInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.FuenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Fuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.ModuloFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.TipoFuente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IFuenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IFuenteServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IModuloFuenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j // Logger
@Service
public class FuenteServicio implements IFuenteServicio {
  private final IFuenteRepositorioBD fuenteRepositorio;
  private final IHechoServicio hechoServicio;
  private final IModuloFuenteServicio moduloFuenteServicio;

  public FuenteServicio(
          IFuenteRepositorioBD fuenteRepositorio,
          IHechoServicio hechoSevicio,
          IModuloFuenteServicio moduloFuenteServicio
  ) {
    this.fuenteRepositorio = fuenteRepositorio;
    this.hechoServicio = hechoSevicio;
    this.moduloFuenteServicio = moduloFuenteServicio;
  }


  @Override
  public FuenteOutputDTO crear(ModuloFuente modulo, FuenteInputDTO i) {
    // Verificar si ya existe una fuente con el mismo módulo e identificador externo (transacción automática del repo)
    Optional<Fuente> fuenteExistente = fuenteRepositorio.findByModuloAndIdentificadorExterno(modulo, i.getIdentificador());

    if (fuenteExistente.isPresent()) {
      log.debug("<FUENTE> Ya existe una fuente con módulo {} e identificador externo {}. Se omite la creación.",
          modulo.getId(), i.getIdentificador());
      return null; // Retornar null para indicar que no se creó una nueva fuente
    }

    // Guardar la fuente (transacción automática del repo)
    Fuente f = new Fuente(modulo, i.getIdentificador(), i.getFechaDeCreacion());
    fuenteRepositorio.save(f);
    log.info("<FUENTE> Creada nueva fuente con id {} para módulo {} e identificador externo {}",
        f.getId(), modulo.getId(), i.getIdentificador());

    // Pedir hechos de forma síncrona y guardar en batch
    if (!this.esMetamapa(f)) {
      try {
        List<Hecho> hechos = this.pedirHechosDeFuente(f);
        if (!hechos.isEmpty()) {
          hechoServicio.guardarTodos(hechos);
          log.info("<FUENTE {}> Guardados {} hechos en batch", f.getId(), hechos.size());
        }
      } catch (Exception e) {
        log.error("<FUENTE {}> Error al obtener/guardar hechos: {}", f.getId(), e.getMessage());
      }
    }

    return f.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Fuente> encontrarTodas() {
    return fuenteRepositorio.findAll();
  }

  @Transactional(readOnly = true)
  public List<FuenteOutputDTO> encontrarTodasDTO() {
    return fuenteRepositorio.findAll().stream().map(Fuente::toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Fuente encontrarPorId(Long identificador) {
    return fuenteRepositorio.findById(identificador).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: "));
  }

  private List<Hecho> pedirHechosDeFuente(Fuente fuente) {
    List<HechoInputDTO> response = fuente.getModulo().getApi().get()
            .uri(uriBuilder -> uriBuilder
                    .path("/fuentes/{id}") // TODO: Acá pedir los hechos filtrados por fecha de modificacion y de creacion
                    .build(fuente.getIdentificadorExterno()))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HechoInputDTO>>() {
            })
            .block();

    if (response == null) {
      return Collections.emptyList();
    }
    List<Hecho> hechos = response.stream().map(hechoServicio::hechoInputDTO).toList();
    hechos.forEach(h -> h.setFuente(fuente)); // Le añadimos la fuente de la que provino
    return hechos;
  }

  //sobrecarga de pedirHechosFuente para filtrar por fecha
  private List<Hecho> pedirHechosDeFuente(Fuente fuente, LocalDateTime fechaAnteriorA) {
    log.debug("Pidiendo hechos de fuente {} (idExterno: {}) con fechaAnteriorA: {}",
        fuente.getId(), fuente.getIdentificadorExterno(), fechaAnteriorA);

    List<HechoInputDTO> response = fuente.getModulo().getApi().get()
            .uri(uriBuilder -> uriBuilder
                    .path("/fuentes/{id}")
                    .queryParam("fechaAnteriorA", fechaAnteriorA.toString())
                    .build(fuente.getIdentificadorExterno()))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<HechoInputDTO>>() {
            })
            .block();

    if (response == null) {
      log.warn("Fuente {} devolvio null", fuente.getId());
      return Collections.emptyList();
    }

    log.debug("Fuente {} devolvio {} hechos nuevos", fuente.getId(), response.size());

    List<Hecho> hechos = response.stream().map(hechoServicio::hechoInputDTO).toList();
    hechos.forEach(h -> h.setFuente(fuente)); // Le añadimos la fuente de la que provino
    return hechos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Hecho> hechosDeFuente(Long identificador) {
    Fuente f = fuenteRepositorio.findById(identificador).orElseThrow(() -> new RuntimeException("Servicio no encontrado con id: "));
    if (this.esMetamapa(f)) {
      return this.pedirHechosDeFuente(f);
    }
    return hechoServicio.hechosDeFuente(f);
  }

  private Boolean esMetamapa(Fuente f) {
    return f.getModulo().getTipo() == TipoFuente.PROXY && f.getIdentificadorExterno() > 1;
  }

  private List<Hecho> nuevosHechos(Fuente fuente, LocalDateTime ultimoChequeo) {
    return this.pedirHechosDeFuente(fuente, ultimoChequeo);
  }

  @Override
  public void refrescarHechosDeFuentes(LocalDateTime ultimoRefresco) {
    // Obtener fuentes en transacción de lectura corta
    List<Fuente> fuentes = fuenteRepositorio.findAll();
    int totalHechosNuevos = 0;
    int fuentesProcesadas = 0;

    // Refrescar cada fuente de forma síncrona
    for (Fuente f : fuentes) {
      if (this.esMetamapa(f)) {
        continue;
      }
      try {
        List<Hecho> hechosNuevos = this.nuevosHechos(f, ultimoRefresco);
        if (!hechosNuevos.isEmpty()) {
          hechoServicio.guardarTodos(hechosNuevos);
          totalHechosNuevos += hechosNuevos.size();
        }
        fuentesProcesadas++;
      } catch (Exception e) {
        log.error("Error al refrescar fuente {}: {}", f.getId(), e.getMessage());
        fuentesProcesadas++;
      }
    }

    if (totalHechosNuevos > 0) {
      log.info("Refresco completado: {} fuentes procesadas, {} hechos nuevos guardados",
          fuentesProcesadas, totalHechosNuevos);
    } else {
      log.debug("Refresco completado: {} fuentes procesadas, sin hechos nuevos", fuentesProcesadas);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public HechoOutputDTO hechoDeFuenteDinamica(Long id) {
    ModuloFuente moduloDinamico = this.moduloFuenteServicio.findByTipoFuente(TipoFuente.DINAMICA);

    Fuente fuente = fuenteRepositorio.findByModulo(moduloDinamico).orElseThrow();

    return hechoServicio.findByFuenteIdAndIdExterno(fuente.getId(), id);
  }

  @Transactional(readOnly = true)
  public Optional<Hecho> encontrarHechoPorTitulo(Fuente fuente, String tituloBuscado) {
    return this.hechoServicio.hechosDeFuente(fuente).stream().filter(h -> h.getTitulo().equals(tituloBuscado)).findFirst();
  }
}
