package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.controladores.publica;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones.NotFoundException;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.AuthResponseDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.UsuarioDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Administrador;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IAdministradorServicio;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/usuarios")
public class UsuarioControlador {
  private final IContribuyenteServicio contribuyenteServicio;
  private final IAdministradorServicio administradorServicio;
  private final PasswordEncoder passwordEncoder;

  public UsuarioControlador(IContribuyenteServicio contribuyenteServicio, IAdministradorServicio administradorServicio) {
    this.contribuyenteServicio = contribuyenteServicio;
    this.administradorServicio = administradorServicio;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @GetMapping("/{username}")
  public UsuarioDTO encontrarUsuario(@PathVariable("username") String username) {
    Contribuyente contribuyente = contribuyenteServicio.findByUsername(username);
    if (contribuyente != null) {
      log.info(contribuyente.toString());
      return new UsuarioDTO(contribuyente.getId(), contribuyente.getUsername(), contribuyente.getPassword(), "CONTRIBUYENTE", new ArrayList<>());
    }

    Administrador administrador = administradorServicio.findByUsername(username);
    if (administrador != null) {
      log.info(administrador.toString());
      return new UsuarioDTO(administrador.getId(), administrador.getUsername(), administrador.getPassword(), "ADMIN", new ArrayList<>());
    }

    return null;
  }

  @GetMapping("/user/{username}")
  public ResponseEntity<UsuarioDTO> obtenerAlumnoPorLegajo(@PathVariable String username) {
    try {
      log.info("Obteniendo al usuario {}", username);
      UsuarioDTO usuario = null;
      Contribuyente contribuyente = contribuyenteServicio.findByUsername(username);
      if (contribuyente != null) {
        log.info(contribuyente.toString());
        usuario = new UsuarioDTO(contribuyente.getId(), contribuyente.getUsername(), contribuyente.getPassword(), "CONTRIBUYENTE", new ArrayList<>());
      } else {
        Administrador administrador = administradorServicio.findByUsername(username);
        if (administrador != null) {
          log.info(administrador.toString());
          usuario = new UsuarioDTO(administrador.getId(), administrador.getUsername(), administrador.getPassword(), "ADMIN", new ArrayList<>());
        }
      }

      return ResponseEntity.ok(usuario);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      return ResponseEntity.notFound().build();
    }
  }

  /*@PostMapping("/usuarios")
  public ResponseEntity<AlumnoDTO> crearAlumno(@RequestBody AlumnoDTO alumnoDTO) {
    try {
      log.info("Creando al alumno {}", alumnoDTO);
      AlumnoDTO alumnoCreado = alumnoService.crearAlumno(alumnoDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(alumnoCreado);
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }*/

  /*@PutMapping("/alumnos/{legajo}")
  public ResponseEntity<AlumnoDTO> actualizarAlumno(@PathVariable String legajo,
                                                    @RequestBody AlumnoDTO alumnoDTO) {
    try {
      log.info("Actualizando al alumno {}", legajo);
      AlumnoDTO alumnoActualizado = alumnoService.actualizarAlumno(legajo, alumnoDTO);
      return ResponseEntity.ok(alumnoActualizado);
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }*/

  /*@DeleteMapping("/alumnos/{legajo}")
  public ResponseEntity<Void> eliminarAlumno(@PathVariable String legajo) {
    try {
      log.info("Eliminando al alumno {}", legajo);
      alumnoService.eliminarAlumno(legajo);
      return ResponseEntity.noContent().build();
    } catch (NotFoundException e) {
      log.error(e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      log.error(e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }*/
}

