package ar.utn.ba.ddsi.ImplementacionClienteLiviano.service;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.AgregadorClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.clients.FuenteDinamicaClient;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.dto.*;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UsuarioService implements UserDetailsService {
  private final AgregadorClient agregadorClient;
  private final FuenteDinamicaClient fuenteDinamicaClient;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(AgregadorClient agregadorClient, FuenteDinamicaClient fuenteDinamicaClient, PasswordEncoder passwordEncoder) {
    this.agregadorClient = agregadorClient;
    this.fuenteDinamicaClient = fuenteDinamicaClient;
    this.passwordEncoder = passwordEncoder;
  }



  /**
   * @deprecated este trabajo ahora lo hace CustomAuthProvider junto a GestionUsuarioApiService
   */
  @Deprecated
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
    //para probar sin necesidad de agregador
    /*if (username.equals("admin")) {
      List<GrantedAuthority> autoridades = new ArrayList<>();
      //spring security requiere que los roles empiecen con ROLE_
      autoridades.add(new SimpleGrantedAuthority("ROLE_admin"));
      return User.withUsername(username)
          .password(passwordEncoder.encode("admin"))
          .authorities(autoridades)
          .build();
    }*/
    log.info("iniciando loadUserByUsername");
    UsuarioDTO usuario = agregadorClient.obtenerUsuarioPorNombre(username);
    if (usuario == null) {
      throw new UsernameNotFoundException(username);
    }
    log.info("Usuario encontrado: " + usuario.getUsername() + ": " + usuario.getPassword());
    List<GrantedAuthority> autoridades = new ArrayList<>();
    usuario.getPermisos().forEach(permiso -> {
      autoridades.add(new SimpleGrantedAuthority(permiso));
    });
    //spring security requiere que los roles empiecen con ROLE_
    autoridades.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    log.info("usuario: " + usuario.getUsername() + ", autoridades: " + autoridades);

    return new CustomUserDetails(
            usuario.getUsername(),
            usuario.getPassword(),
            autoridades,
            usuario.getId()
    );
  }

  public ContribuyenteOutputDTO registrarContribuyente(RegisterDTO registerDTO) {
    ContribuyenteOutputDTO nuevoContribuyente = new ContribuyenteOutputDTO(
            null,
            registerDTO.getNombre(),
            registerDTO.getApellido(),
            registerDTO.getEdad(),
            registerDTO.getEmail(),
            passwordEncoder.encode(registerDTO.getPassword()),
            registerDTO.getUsername()
    );
    try {
      ContribuyenteOutputDTO confirmado = agregadorClient.registrarContribuyente(nuevoContribuyente);
      // Convertir edad de String a Integer para fuenteDinamica
      Integer edadInt = confirmado.getEdad() != null ? Integer.parseInt(confirmado.getEdad()) : null;
      fuenteDinamicaClient.registrarContribuyente(new ContribuyenteDinamicaOutputDTO(
              confirmado.getId(),
              confirmado.getNombre(),
              confirmado.getApellido(),
              edadInt));
      return confirmado;
    } catch (WebClientResponseException e) {
      // Leer el JSON del backend agregador
      String body = e.getResponseBodyAsString(); // Ej: {"error":"El usuario ya existe"}
      throw new RuntimeException(body); // lo reenvías hacia arriba
    } catch (RuntimeException e) {
      // Capturar errores de fuenteDinamica u otros servicios
      throw new RuntimeException(e.getMessage());
    }
  }

  public AdministradorOutputDTO registrarAdministrador(RegisterAdminDTO registerDTO) {
    AdministradorOutputDTO nuevoAdministrador = new AdministradorOutputDTO();
    nuevoAdministrador.setUsername(registerDTO.getUsername());
    nuevoAdministrador.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
    try {
      AdministradorOutputDTO confirmado = agregadorClient.registrarAdministrador(nuevoAdministrador);
      fuenteDinamicaClient.registrarAdministrador(new AdministradorDinamicaOutputDTO(
              confirmado.getIdentificador(),
              confirmado.getUsername()));

      return confirmado;
    } catch (WebClientResponseException e) {
      // Leer el JSON del backend agregador
      String body = e.getResponseBodyAsString(); // Ej: {"error":"El usuario ya existe"}
      throw new RuntimeException(body); // lo reenvías hacia arriba
    }
  }
}
