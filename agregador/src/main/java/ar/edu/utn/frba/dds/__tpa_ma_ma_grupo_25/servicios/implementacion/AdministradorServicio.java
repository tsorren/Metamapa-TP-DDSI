package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IAdministradorRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
//import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Slf4j // Logger
@Service
public class AdministradorServicio implements IAdministradorServicio {
  private final IAdministradorRepositorioBD administradorRepositorio;
  private final PasswordEncoder passwordEncoder;

  public AdministradorServicio(
          IAdministradorRepositorioBD a,
          @Value("${default.admin.name}") String defaultAdminName,
          @Value("${default.admin.password}") String defaultAdminPassword
  ) {
    this.administradorRepositorio = a;
    this.passwordEncoder = new BCryptPasswordEncoder();

    if (administradorRepositorio.findByUsername(defaultAdminName)==null){
      this.crear(defaultAdminName, defaultAdminPassword);
    }
  }

  @Override
  @Transactional
  public AdministradorOutputDTO crear(String username, String password) {
    Administrador a = new Administrador();
    a.setUsername(username);
    a.setPassword(passwordEncoder.encode(password));
    administradorRepositorio.save(a);
    AdministradorOutputDTO b = a.toDTO();
    log.info("<CREACION> Administrador creado con id {}", a.getId());
    return b;
  }


  @Override
  @Transactional(readOnly = true)
  public Administrador findByUsername(String username) {
    return administradorRepositorio.findByUsername(username);
  }
}
