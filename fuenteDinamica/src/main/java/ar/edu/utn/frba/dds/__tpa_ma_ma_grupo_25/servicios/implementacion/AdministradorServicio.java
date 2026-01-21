package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.AdministradorInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IAdministradorRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j // Logger
@Service
public class AdministradorServicio implements IAdministradorServicio {
  private final IAdministradorRepositorioBD administradorRepositorio;

  public AdministradorServicio(IAdministradorRepositorioBD a, @Value("${default.admin.name}") String defaultAdminName) {
    this.administradorRepositorio = a;

    if (administradorRepositorio.findByUsername(defaultAdminName)==null){
      this.crear(new AdministradorInputDTO(1L, defaultAdminName));
    }
  }
  @Override
  @Transactional
  public AdministradorOutputDTO crear(AdministradorInputDTO administrador) {
    Administrador a = new Administrador(administrador.getIdentificador(), administrador.getUsername());
    administradorRepositorio.save(a);

    log.info("<CREACION> Administrador creado con id {}", a.getId());
    return a.toDTO();
  }
}
