package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


//existe solo para tratar el tema de las sesiones mixtas
//no se persiste
@Data
public class Usuario {

  private Long id;
  private String username;
  private String contrasenia;
  private Rol rol;
  private List<Permiso> permisos;

  public Usuario() {
    this.permisos = new ArrayList<>();
  }

  public void agregarPermiso(Permiso permiso) {
    this.permisos.add(permiso);
  }
}