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