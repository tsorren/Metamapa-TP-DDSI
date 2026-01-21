package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.AdministradorInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.AdministradorOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdministradorControlador {
  private final IAdministradorServicio administradorServicio;

  public AdministradorControlador(IAdministradorServicio administradorServicio) {
    this.administradorServicio = administradorServicio;
  }

  @PostMapping()
  public AdministradorOutputDTO crear(@RequestBody AdministradorInputDTO administrador) {
    return administradorServicio.crear(administrador);
  }
}
