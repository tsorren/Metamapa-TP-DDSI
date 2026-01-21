package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones.NotFoundException;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.RolesPermisosDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Rol;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Usuario;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IAdministradorRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContribuyenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.ILoginService;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService implements ILoginService {
  private final BCryptPasswordEncoder passwordEncoder;
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;

  public LoginService(IAdministradorRepositorioBD administradorRepositorio, IContribuyenteRepositorioBD contribuyenteRepositorio) {
    this.administradorRepositorio = administradorRepositorio;
    this.contribuyenteRepositorio = contribuyenteRepositorio;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public Usuario autenticarUsuario(String username, String password) {
    Usuario usuario = this.encontrarUsuario(username);

    if (usuario == null) {
      throw new NotFoundException("Usuario", username);
    }

    //Usuario usuario = usuarioOpt.get();

    // Verificar la contraseña usando BCrypt
    if (!passwordEncoder.matches(password, usuario.getContrasenia())) {
      throw new NotFoundException("Usuario", username);
    }

    return usuario;
  }

  public String generarAccessToken(String username) {
    Usuario usuario = this.encontrarUsuario(username);
    return JwtUtil.generarAccessToken(username, usuario.getRol().toString());
  }

  public String generarRefreshToken(String username) {
    return JwtUtil.generarRefreshToken(username);
  }

  public RolesPermisosDTO obtenerRolesYPermisosUsuario(String username) {
    Usuario usuario = new Usuario();
    Contribuyente contribuyente = contribuyenteRepositorio.findByUsername(username);
    if (contribuyente != null) {
      usuario.setId(contribuyente.getId());
      usuario.setUsername(contribuyente.getUsername());
      usuario.setContrasenia(contribuyente.getPassword());
      usuario.setRol(Rol.CONTRIBUYENTE);
    }else{
      Administrador admin = administradorRepositorio.findByUsername(username);
      if (admin != null) {
        usuario.setId(admin.getId());
        usuario.setUsername(admin.getUsername());
        usuario.setContrasenia(admin.getPassword());
        usuario.setRol(Rol.ADMIN);
      }
    }

    if (usuario.getId() == null) {
      throw new NotFoundException("Usuario", username);
    }

    //Usuario usuario = usuarioOpt.get();

    return RolesPermisosDTO.builder()
        .username(usuario.getUsername())
        .rol(usuario.getRol())
        .permisos(usuario.getPermisos())
        .build();
  }

  private Usuario encontrarUsuario(String username){
    Usuario usuario = new Usuario();
    Contribuyente contribuyente = contribuyenteRepositorio.findByUsername(username);
    if (contribuyente != null) {
      usuario.setId(contribuyente.getId());
      usuario.setUsername(contribuyente.getUsername());
      usuario.setContrasenia(contribuyente.getPassword());
      usuario.setRol(Rol.CONTRIBUYENTE);
      return usuario;
    }else{
      Administrador admin = administradorRepositorio.findByUsername(username);
      if (admin != null) {
        usuario.setId(admin.getId());
        usuario.setUsername(admin.getUsername());
        usuario.setContrasenia(admin.getPassword());
        usuario.setRol(Rol.ADMIN);
        return usuario;
      }
    }
    return null;
  }
}
