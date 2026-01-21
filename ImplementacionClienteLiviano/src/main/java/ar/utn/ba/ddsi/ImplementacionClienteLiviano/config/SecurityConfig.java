package ar.utn.ba.ddsi.ImplementacionClienteLiviano.config;

import ar.utn.ba.ddsi.ImplementacionClienteLiviano.providers.CustomAuthProvider;
import ar.utn.ba.ddsi.ImplementacionClienteLiviano.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UsuarioService usuarioService;
  private final PasswordEncoder passwordEncoder;
  private final CustomAuthenticationSuccessHandler successHandler;

  public SecurityConfig(UsuarioService usuarioService, PasswordEncoder passwordEncoder,
                       CustomAuthenticationSuccessHandler successHandler) {
    this.usuarioService = usuarioService;
    this.passwordEncoder = passwordEncoder;
    this.successHandler = successHandler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authz -> authz
            // Recursos estáticos - siempre públicos
            .requestMatchers("/css/**", "/js/**", "/images/**", "/iconos/**").permitAll()
            // MULTIMEDIA - Debe ser público para que el navegador pueda cargar imágenes/videos/audio
            .requestMatchers("/multimedia/**").permitAll()
            // Rutas públicas - accesibles sin autenticación
            .requestMatchers("/", "/landing", "/login", "/register").permitAll()
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/home").permitAll()
            // IMPORTANTE: Colecciones debe ser accesible para todos (visualizadores)
            .requestMatchers("/colecciones", "/colecciones/**").permitAll()
            .requestMatchers("/visualizarHechos", "/visualizarHechos/**").permitAll()
            // Rutas de administrador - requieren ROLE_ADMIN
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/panel/**").hasRole("ADMIN")
            .requestMatchers("/api/observabilidad/**").hasRole("ADMIN")
            // Rutas de contribuyente - requieren autenticación
            .requestMatchers("/cargarHecho", "/cargarHecho/**").authenticated()
            .requestMatchers("/hechos-creados", "/hechos-ceados/**").authenticated()
            // Rutas de notificaciones - requieren autenticación
            .requestMatchers("/notificaciones/**").authenticated()
            // Cualquier otra ruta - por defecto permitir (cambio de estrategia)
            .anyRequest().permitAll()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .failureUrl("/login?error=true")
            .successHandler(successHandler)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .permitAll()
        )
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, ex2) -> res.sendRedirect("/login?unauthorized"))
                .accessDeniedHandler((req, res, ex2) -> res.sendRedirect("/403"))
        )
        /*.userDetailsService(usuarioService)*/;

    return http.build();
  }

  /**
   * ⚙️ Conecta tu UsuarioService al sistema de autenticación
   */
  /*@Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);

    authBuilder
        .userDetailsService(usuarioService)
        .passwordEncoder(passwordEncoder);

    return authBuilder.build();
  }*/

  //esto esta para la sesiones mixtas
  //si deja de funcionar la pagina comentar esto y descomentar lo de arriva
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http, CustomAuthProvider provider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }
}
