package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.hecho.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NormalizadorCategoria {

  private final ICategoriaRepositorioBD categoriaRepositorio;
  private Normalizador<Categoria> normalizador;

  @Value("${normalizador.categoria.umbral-similitud}")
  private float umbralSimilitud;

  public NormalizadorCategoria(ICategoriaRepositorioBD categoriaRepositorio) {
    this.categoriaRepositorio = categoriaRepositorio;
  }

  @PostConstruct
  public void init() {
    List<String> categorias = categoriaRepositorio.findAllNames();

    this.normalizador = new Normalizador<>(
            categorias,
            categoriaRepositorio::findByNombre,
            Categoria::getNombre,
            nombre -> {
              Categoria nueva = new Categoria(nombre);
              categoriaRepositorio.save(nueva);
              return nueva;
            },
            umbralSimilitud
    );
  }

  public Categoria normalizar(Categoria categoria) {
    return normalizador.normalizar(categoria);
  }
}
