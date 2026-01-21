package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class AgregadorAplicacion {

  public static void main(String[] args) {
    SpringApplication.run(AgregadorAplicacion.class, args);
  }

}