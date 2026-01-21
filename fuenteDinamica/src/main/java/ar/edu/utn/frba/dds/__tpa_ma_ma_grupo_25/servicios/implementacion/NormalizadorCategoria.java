package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ISincronizadorCategoriaServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NormalizadorCategoria {

  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final Normalizador normalizadorGenerico;
  private final ISincronizadorCategoriaServicio sincronizadorCategoria;

  @Value("${normalizador.categoria.umbral-similitud}")
  private float umbralSimilitud;

  public NormalizadorCategoria(ICategoriaRepositorioBD categoriaRepositorio,
                                ISincronizadorCategoriaServicio sincronizadorCategoria) {
    this.categoriaRepositorio = categoriaRepositorio;
    this.normalizadorGenerico = new Normalizador();
    this.sincronizadorCategoria = sincronizadorCategoria;
  }

  public Categoria normalizar(Categoria categoria) {
    if (categoria == null || categoria.getNombre() == null) return null;

    String nombreEntrada = categoria.getNombre().trim();

    // Paso 1: Buscar coincidencia exacta case-insensitive
    List<String> nombresExistentes = categoriaRepositorio.findAllNames();
    for (String nombreExistente : nombresExistentes) {
      if (nombreExistente.equalsIgnoreCase(nombreEntrada)) {
        // Encontró la misma palabra con diferente capitalización -> usar la existente
        return categoriaRepositorio.findByNombre(nombreExistente);
      }
    }

    // Paso 2: Buscar similitud con Jaro-Winkler (para typos y variaciones)
    String nombreNormalizado = normalizadorGenerico.normalizar(nombreEntrada, nombresExistentes, umbralSimilitud);
    if (nombreNormalizado != null) {
      return categoriaRepositorio.findByNombre(nombreNormalizado);
    }

    // Paso 3: No existe ninguna similar -> crear nueva con formato capitalizado
    String nombreCanonical = capitalizarPrimeraLetra(nombreEntrada);
    Categoria nuevaCategoria = new Categoria(nombreCanonical);
    categoriaRepositorio.save(nuevaCategoria);

    // Sincronizar con otros microservicios
    sincronizadorCategoria.sincronizarCategoriaEnTodosLosMicroservicios(nuevaCategoria);

    return nuevaCategoria;
  }

  /**
   * Capitaliza la primera letra de cada palabra para mantener consistencia.
   * Ejemplo: "incendio forestal" -> "Incendio Forestal"
   */
  private String capitalizarPrimeraLetra(String texto) {
    if (texto == null || texto.isEmpty()) return texto;

    String[] palabras = texto.toLowerCase().split("\\s+");
    StringBuilder resultado = new StringBuilder();

    for (int i = 0; i < palabras.length; i++) {
      String palabra = palabras[i];
      if (!palabra.isEmpty()) {
        resultado.append(Character.toUpperCase(palabra.charAt(0)));
        if (palabra.length() > 1) {
          resultado.append(palabra.substring(1));
        }
      }
      if (i < palabras.length - 1) {
        resultado.append(" ");
      }
    }

    return resultado.toString();
  }
}
