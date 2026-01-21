package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Hecho;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Multimedia;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Ubicacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.origenes.TipoOrigen;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContenidoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IHechoRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IHechoServicio;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HechoServicio implements IHechoServicio {

  @Getter
  private final IHechoRepositorioBD hechoRepositorio;
  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final IContenidoRepositorioBD contenidoRepositorio;
  private final NormalizadorCategoria normalizadorCategoria;

  public HechoServicio(
          IHechoRepositorioBD hechoRepositorio,
          ICategoriaRepositorioBD categoriaRepositorio,
          IContenidoRepositorioBD contenidoRepositorio,
          NormalizadorCategoria normalizadorCategoria
  ) {
    this.hechoRepositorio = hechoRepositorio;
    this.categoriaRepositorio = categoriaRepositorio;
    this.contenidoRepositorio = contenidoRepositorio;
    this.normalizadorCategoria = normalizadorCategoria;
  }

  // -------------------- Convertir Hecho a DTO --------------------
  public static HechoOutputDTO hechoOutputDTO(Hecho h) {
    return new HechoOutputDTO(
            h.getId(),
            h.getTitulo(),
            h.getDescripcion(),
            h.getCategoria(),
            h.getUbicacion(),
            h.getFechaDelHecho(),
            h.getFechaDeCarga(),
            h.getFechaUltimaModificacion(),
            h.getContenido(),
            h.getOrigen(),
            h.getEtiquetas(),
            h.getVisible()
    );
  }

  // -------------------- Métodos de interfaz --------------------
  @Override
  public List<HechoOutputDTO> encontrarTodos() {
    return hechoRepositorio.findAll()
            .stream()
            .map(HechoServicio::hechoOutputDTO)
            .collect(Collectors.toList());
  }

  @Override
  public HechoOutputDTO encontrarPorId(Long id) {
    return hechoRepositorio.findById(id)
            .map(HechoServicio::hechoOutputDTO)
            .orElseThrow();
  }

  @Override
  public Hecho crear(String titulo, String descripcion, Categoria categoria,
                     Double latitud, Double longitud, LocalDate fechaDelHecho,
                     Long idContenido, Boolean visible) {

    Categoria categoriaNormalizada = normalizadorCategoria.normalizar(categoria);
    categoriaRepositorio.save(categoriaNormalizada);

    Ubicacion ubicacion = new Ubicacion(latitud, longitud);

    Multimedia contenido = null;
    if (idContenido != null) {
      contenido = contenidoRepositorio.findById(idContenido).orElse(null);
    }

    Hecho hecho = Hecho.crear(titulo, descripcion, categoriaNormalizada, ubicacion, fechaDelHecho, TipoOrigen.CONTRIBUYENTE);

    hecho.setContenido(contenido);
    hecho.setVisible(visible != null ? visible : true);

    hechoRepositorio.save(hecho);

    log.info("<CREADO> Hecho con id {}", hecho.getId());
    return hecho;
  }

  public Hecho save(Hecho hecho) {
    return hechoRepositorio.save(hecho);
  }


  @Override
  public void ocultarHecho(Long idHecho) {
    Hecho h = hechoRepositorio.findById(idHecho).orElseThrow();
    h.ocultar();
    hechoRepositorio.save(h);
    log.info("<OCULTADO> Hecho con id {}", idHecho);
  }
}
