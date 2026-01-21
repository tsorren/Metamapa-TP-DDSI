package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.implementacion;

import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.excepciones.UsuarioExistenteException;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.ContribuyenteInputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.output.ContribuyenteOutputDTO;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.usuario.Contribuyente;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.repositorios.IContribuyenteRepositorioBD;
import ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.servicios.IContribuyenteServicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
public class ContribuyenteServicio implements IContribuyenteServicio {
  private final IContribuyenteRepositorioBD contribuyenteRepositorio;

  public ContribuyenteServicio(IContribuyenteRepositorioBD contribuyenteRepositorio) {
    this.contribuyenteRepositorio = contribuyenteRepositorio;
  }

  @Override
  @Transactional
  public ContribuyenteOutputDTO crear(ContribuyenteInputDTO i) {
    if (this.findByUsername(i.getUsername()) != null) {
      throw new UsuarioExistenteException("El nombre de usuario ya está registrado");
    }
    if (this.findByUserEmail(i.getMail()) != null) {
      throw new UsuarioExistenteException("Este email ya esta registrado");
    }

    Contribuyente contribuyente = Contribuyente.crear(i.getUsername(), i.getPassword(), i.getMail());
    contribuyente.setNombre(i.getNombre());
    contribuyente.setApellido(i.getApellido());
    if(i.getEdad()!=null) {
      LocalDate fecha = LocalDate.parse(i.getEdad()); // formato YYYY-MM-DD
      contribuyente.setEdad(calcularEdad(fecha));
    }
    contribuyenteRepositorio.save(contribuyente);

    log.info("<CREACION> Contribuyente creado con id {}", contribuyente.getId());
    return contribuyente.toDTO();
  }

  @Override
  @Transactional(readOnly = true)
  public Contribuyente findByUsername(String username) {
    return contribuyenteRepositorio.findByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public Contribuyente findByUserEmail(String userEmail) {
    return contribuyenteRepositorio.findByMail(userEmail);
  }

  public int calcularEdad(LocalDate fechaNacimiento) {
    if (fechaNacimiento == null) {
      return -1; // o lanzar excepción, según tu caso
    }
    return Period.between(fechaNacimiento, LocalDate.now()).getYears();
  }
}
