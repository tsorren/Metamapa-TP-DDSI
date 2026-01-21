package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.entidades.fuentes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para persistir la configuración de una FuenteProxy.
 * Almacena los datos necesarios para reconstruir una FuenteProxy (Desastres o Metamapa).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fuente_proxy_config")
public class FuenteProxyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoFuenteProxy tipo;

    @Column(name = "nombre")
    private String nombre;

    // Credenciales para FuenteDesastres (opcional, solo se usa si tipo = DESASTRESAPI)
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "activa")
    private Boolean activa;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activa == null) {
            activa = true;
        }
    }

    /**
     * Crea una configuración para FuenteDesastres
     */
    public static FuenteProxyConfig crearDesastres(String baseUrl, String email, String password, String nombre) {
        return FuenteProxyConfig.builder()
                .baseUrl(baseUrl)
                .tipo(TipoFuenteProxy.DESASTRESAPI)
                .nombre(nombre != null ? nombre : "Desastres API")
                .email(email)
                .password(password)
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
    }

    /**
     * Crea una configuración para FuenteMetamapa
     */
    public static FuenteProxyConfig crearMetamapa(String baseUrl, String nombre) {
        return FuenteProxyConfig.builder()
                .baseUrl(baseUrl)
                .tipo(TipoFuenteProxy.METAMAPA)
                .nombre(nombre != null ? nombre : "Metamapa")
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
    }
}

