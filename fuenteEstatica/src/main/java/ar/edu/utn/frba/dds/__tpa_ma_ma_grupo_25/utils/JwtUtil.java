package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
  @Getter
  private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 min
  private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 días

  public static String generarAccessToken(String username, String rol) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuer("gestion-usuarios-server")
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
        .claim("rol", rol)
        .signWith(key)
        .compact();
  }

  public static String generarRefreshToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuer("gestion-usuarios-server")
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
        .claim("type", "refresh") // diferenciamos refresh del access
        .signWith(key)
        .compact();
  }

  public static String validarToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public static Claims obtenerClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)   // misma clave usada para firmar
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}