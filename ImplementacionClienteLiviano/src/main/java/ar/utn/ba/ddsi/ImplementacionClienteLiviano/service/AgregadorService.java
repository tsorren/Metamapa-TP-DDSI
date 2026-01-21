package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgregadorService {

  private final AgregadorClient agregadorClient;

  public AgregadorService(AgregadorClient agregadorClient) {
    this.agregadorClient = agregadorClient;
  }

  public List<ColeccionDTO> listarColecciones() {
    return agregadorClient.obtenerColecciones();
  }

  public ConsultarColeccionDTO obtenerColeccionDestacada() {
    return agregadorClient.obtenerColeccionDestacada();
  }

  public ConsultarColeccionDTO obtenerColeccionDestacada(Integer pagina, Integer tamanoPagina) {
    return agregadorClient.obtenerColeccionDestacada(pagina, tamanoPagina);
  }

  public List<FuenteOutputDTO> listarFuentes() {
    return agregadorClient.obtenerFuentesRegistradas();
  }

  public ColeccionDTO obtenerColeccion(String handle) {
    return agregadorClient.obtenerColeccionPorHandle(handle);
  }

  public ConsultarColeccionDTO consultarColeccion(String handle, InformacionFiltrado filtrado) {
    return agregadorClient.consultarColeccion(handle, filtrado);
  }

  public HechoDTO obtenerHecho(Long id) {
    return agregadorClient.obtenerHechoPorId(id);
  }

  public ColeccionDTO crearColeccion(ColeccionInputDTO datos) {
    return agregadorClient.crearColeccion(datos);
  }

  public ColeccionDTO actualizarColeccion(String handle, ColeccionInputDTO datos) {
    return agregadorClient.actualizarColeccion(handle, datos);
  }

  public void eliminarColeccion(String handle) {
    agregadorClient.eliminarColeccion(handle);
  }
}
