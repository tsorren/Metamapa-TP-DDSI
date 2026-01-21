package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableScheduling
public class FuenteDinamicaAplicacion {

  public static void main(String[] args) {
    SpringApplication.run(FuenteDinamicaAplicacion.class, args);
  }

}