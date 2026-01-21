package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.RolesPermisosDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.UsuarioDTO;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.exceptions.NotFoundException;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.internal.WebApiCallerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de Usuarios y autenticación delegada
 */

@Service
public class GestionUsuariosApiService {

  private static final Logger log = LoggerFactory.getLogger(GestionUsuariosApiService.class);
  private final WebClient webClient;
  private final WebApiCallerService webApiCallerService;
  private final String authServiceUrl;
  private final String usuariosServiceUrl;

  public GestionUsuariosApiService(
          WebApiCallerService webApiCallerService,
          @Value("${auth.service.url}") String authServiceUrl, //TODO: implementar urls
          @Value("${usuarios.service.url}") String usuariosServiceUrl) {
    this.webClient = WebClient.builder().build();
    this.webApiCallerService = webApiCallerService;
    this.authServiceUrl = authServiceUrl;
    this.usuariosServiceUrl = usuariosServiceUrl;
  }

  public AuthResponseDTO login(String username, String password) {
    try {
      log.info("Iniciando login para usuario: {}", username);
      AuthResponseDTO response = webClient
              .post()
              .uri(authServiceUrl)
              .bodyValue(Map.of(
                      "username", username,
                      "password", password
              ))
              .retrieve()
              .bodyToMono(AuthResponseDTO.class)
              .block();
      log.info("Usuario: {}, Id: {}, logeado exitosamente", username, response.getId());
      return response;
    } catch (WebClientResponseException e) {
      log.error("Error HTTP en login: {} - {}", e.getStatusCode(), e.getMessage());
      // Credenciales incorrectas o usuario no encontrado
      if (e.getStatusCode() == HttpStatus.NOT_FOUND ||
          e.getStatusCode() == HttpStatus.UNAUTHORIZED ||
          e.getStatusCode() == HttpStatus.BAD_REQUEST) {
        return null;
      }
      // Otros errores HTTP
      throw new RuntimeException("Error en el servicio de autenticación: " + e.getMessage(), e);
    } catch (Exception e) {
      log.error("Error de conexión en login: {}", e.getMessage());
      throw new RuntimeException("Error de conexión con el servicio de autenticación: " + e.getMessage(), e);
    }
  }

  public RolesPermisosDTO getRolesPermisos(String accessToken) {
    try {
      RolesPermisosDTO response = webApiCallerService.getWithAuth(
          authServiceUrl + "/roles-permisos",
          accessToken,
          RolesPermisosDTO.class
      );
      return response;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new RuntimeException("Error al obtener roles y permisos: " + e.getMessage(), e);
    }
  }

  public List<UsuarioDTO> obtenerTodosLosUsuarios() {
    List<UsuarioDTO> response = webApiCallerService.getList(usuariosServiceUrl, UsuarioDTO.class);
    return response != null ? response : List.of();
  }

  public UsuarioDTO obtenerUsuarioPorUsername(String username) {
    UsuarioDTO response = webApiCallerService.get(usuariosServiceUrl + "/user/"+username, UsuarioDTO.class);
    if (response == null) {
      throw new NotFoundException("Usuario", username);
    }
    return response;
  }

  public UsuarioDTO crearUsuario(UsuarioDTO alumnoDTO) {
    UsuarioDTO response = webApiCallerService.post(usuariosServiceUrl, alumnoDTO, UsuarioDTO.class);
    if (response == null) {
      throw new RuntimeException("Error al crear usuario en el servicio externo");
    }
    return response;
  }

  public UsuarioDTO actualizarUsuario(String username, UsuarioDTO usuarioDTO) {
    UsuarioDTO response = webApiCallerService.put(usuariosServiceUrl + "/" + username, usuarioDTO, UsuarioDTO.class);
    if (response == null) {
      throw new RuntimeException("Error al actualizar usuario en el servicio externo");
    }
    return response;
  }

  public void eliminarUsuario(String username) {
    webApiCallerService.delete(usuariosServiceUrl + "/" + username);
  }

  public boolean existeUsuario(String username) {
    try {
      obtenerUsuarioPorUsername(username);
      return true;
    } catch (NotFoundException e) {
      return false;
    } catch (Exception e) {
      throw new RuntimeException("Error al verificar existencia del alumno: " + e.getMessage(), e);
    }
  }
}
