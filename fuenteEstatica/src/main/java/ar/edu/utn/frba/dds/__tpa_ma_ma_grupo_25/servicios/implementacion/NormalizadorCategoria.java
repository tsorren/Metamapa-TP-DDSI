package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.Categoria;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.ICategoriaRepositorioBD;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NormalizadorCategoria {

  private static final long CACHE_TTL = 60000; // 1 minuto
  private final ICategoriaRepositorioBD categoriaRepositorio;
  private final Normalizador normalizadorGenerico;

  @Value("${normalizador.categoria.umbral-similitud}")
  private float umbralSimilitud;

  // Cache para evitar consultas repetitivas
  private final Map<String, Categoria> cacheExacto = new ConcurrentHashMap<>();
  private List<String> nombresExistentesCache = null;
  private long ultimaActualizacionCache = 0;

  public NormalizadorCategoria(ICategoriaRepositorioBD categoriaRepositorio) {
    this.categoriaRepositorio = categoriaRepositorio;
    this.normalizadorGenerico = new Normalizador();
  }

  public Categoria normalizar(Categoria categoria) {
    if (categoria == null || categoria.getNombre() == null) return null;

    String nombreCategoria = categoria.getNombre().trim();

    // Paso 1: Buscar en cache primero (exacto)
    if (cacheExacto.containsKey(nombreCategoria)) {
      return cacheExacto.get(nombreCategoria);
    }

    // Paso 2: Buscar en BD - coincidencia exacta
    Categoria categoriaExacta = categoriaRepositorio.findByNombre(nombreCategoria);
    if (categoriaExacta != null) {
      cacheExacto.put(nombreCategoria, categoriaExacta);
      return categoriaExacta;
    }

    // Actualizar cache de nombres si es necesario
    actualizarCacheNombres();

    // Paso 3: Buscar coincidencia case-insensitive
    for (String nombreExistente : nombresExistentesCache) {
      if (nombreExistente.equalsIgnoreCase(nombreCategoria)) {
        Categoria categoriaExistente = categoriaRepositorio.findByNombre(nombreExistente);
        if (categoriaExistente != null) {
          cacheExacto.put(nombreCategoria, categoriaExistente);
          return categoriaExistente;
        }
      }
    }

    // Paso 4: Buscar categoría similar (Jaro-Winkler)
    String nombreNormalizado = normalizadorGenerico.normalizar(
            nombreCategoria,
            nombresExistentesCache,
            umbralSimilitud
    );

    if (nombreNormalizado != null) {
      Categoria categoriaExistente = categoriaRepositorio.findByNombre(nombreNormalizado);
      if (categoriaExistente != null) {
        cacheExacto.put(nombreCategoria, categoriaExistente);
        return categoriaExistente;
      }
    }

    // Paso 5: No existe -> Guardar nueva categoría con formato capitalizado
    String nombreCanonical = capitalizarPrimeraLetra(nombreCategoria);
    Categoria nuevaCategoria = new Categoria(nombreCanonical);
    nuevaCategoria = categoriaRepositorio.saveAndFlush(nuevaCategoria);
    cacheExacto.put(nombreCategoria, nuevaCategoria);

    // Invalidar cache de nombres para que se actualice
    nombresExistentesCache = null;

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

  private void actualizarCacheNombres() {
    long ahora = System.currentTimeMillis();
    if (nombresExistentesCache == null || (ahora - ultimaActualizacionCache) > CACHE_TTL) {
      nombresExistentesCache = categoriaRepositorio.findAllNames();
      ultimaActualizacionCache = ahora;
    }
  }

  public void limpiarCache() {
    cacheExacto.clear();
    nombresExistentesCache = null;
  }
}
