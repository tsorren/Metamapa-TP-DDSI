package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.middlewares;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class IpFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(IpFilter.class);

  private final IpProperties ipProperties;

  // Si estamos en Railway u otro entorno cloud, deshabilitar filtro de IP
  @Value("${ip.filter.enabled:false}")
  private boolean filterEnabled;

  public IpFilter(IpProperties ipProperties) {
    this.ipProperties = ipProperties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    String ip = extractClientIp(request);
    String path = request.getRequestURI();

    // Si el filtro está deshabilitado, dejar pasar todo
    if (!filterEnabled) {
      log.debug("[IP FILTER] Filtro deshabilitado, permitiendo IP: {} para {}", ip, path);
      filterChain.doFilter(request, response);
      return;
    }

    // Si no pudimos determinar ip, podemos bloquear o dejar pasar según política.
    if (ip == null) {
      log.warn("[IP FILTER] No se pudo obtener IP cliente para {}", path);
      response.sendError(HttpStatus.FORBIDDEN.value(), "No se pudo obtener la IP cliente");
      return;
    }

    // Permitir siempre conexiones internas de Railway (IPs privadas)
    if (isPrivateOrInternalIp(ip)) {
      log.debug("[IP FILTER] IP privada/interna permitida: {} para {}", ip, path);
      filterChain.doFilter(request, response);
      return;
    }

    // Primero: check blacklist (si coincide, bloquear siempre)
    List<String> blacklist = ipProperties.getBlacklist();
    if (blacklist != null) {
      for (String entry : blacklist) {
        if (matches(entry.trim(), ip)) {
          log.warn("[IP FILTER] IP bloqueada (blacklist): {} para {}", ip, path);
          response.sendError(HttpStatus.FORBIDDEN.value(), "IP bloqueada");
          return;
        }
      }
    }

    // Si existe una whitelist no vacía -> solo permitir si coincide whitelist
    List<String> whitelist = ipProperties.getWhitelist();
    if (whitelist != null && !whitelist.isEmpty()) {
      boolean allowed = false;
      for (String entry : whitelist) {
        if (matches(entry.trim(), ip)) {
          allowed = true;
          break;
        }
      }
      if (!allowed) {
        log.warn("[IP FILTER] IP no autorizada (no en whitelist): {} para {}", ip, path);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "IP no autorizada");
        return;
      }
    }

    // Si pasó las comprobaciones, seguir cadena
    log.debug("[IP FILTER] IP permitida: {} para {}", ip, path);
    filterChain.doFilter(request, response);
  }

  /**
   * Verifica si la IP es privada o interna (localhost, Docker, Kubernetes, Railway)
   */
  private boolean isPrivateOrInternalIp(String ip) {
    if (ip == null) return false;

    // Localhost
    if (ip.equals("127.0.0.1") || ip.equals("::1") || ip.equals("0:0:0:0:0:0:0:1")) {
      return true;
    }

    // IPs privadas clase A (10.x.x.x)
    if (ip.startsWith("10.")) {
      return true;
    }

    // IPs privadas clase B (172.16.x.x - 172.31.x.x)
    if (ip.startsWith("172.")) {
      try {
        int secondOctet = Integer.parseInt(ip.split("\\.")[1]);
        if (secondOctet >= 16 && secondOctet <= 31) {
          return true;
        }
      } catch (Exception e) {
        // Ignorar errores de parseo
      }
    }

    // IPs privadas clase C (192.168.x.x)
    if (ip.startsWith("192.168.")) {
      return true;
    }

    // Link-local (169.254.x.x)
    if (ip.startsWith("169.254.")) {
      return true;
    }

    return false;
  }

  /**
   * Extrae la IP real teniendo en cuenta proxies:
   * Prioridad: X-Forwarded-For (primer elemento) > X-Real-IP > request.getRemoteAddr()
   */
  private String extractClientIp(HttpServletRequest request) {
    String xff = request.getHeader("X-Forwarded-For");
    if (StringUtils.hasText(xff)) {
      String[] parts = xff.split(",");
      if (parts.length > 0) {
        return parts[0].trim();
      }
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (StringUtils.hasText(xRealIp)) {
      return xRealIp.trim();
    }

    return request.getRemoteAddr();
  }

  private boolean matches(String entry, String ip) {
    if (entry.contains("/")) {
      try {
        return isInRangeIPv4(ip, entry);
      } catch (Exception e) {
        log.warn("[IP FILTER] Error verificando CIDR {}: {}", entry, e.getMessage());
        return false;
      }
    } else {
      return entry.equals(ip);
    }
  }

  private boolean isInRangeIPv4(String ip, String cidr) {
    try {
      String[] parts = cidr.split("/");
      String networkAddress = parts[0];
      int prefixLength = Integer.parseInt(parts[1]);

      InetAddress network = InetAddress.getByName(networkAddress);
      InetAddress address = InetAddress.getByName(ip);

      byte[] networkBytes = network.getAddress();
      byte[] addressBytes = address.getAddress();

      int mask = -1 << (32 - prefixLength);

      int networkInt = ((networkBytes[0] & 0xFF) << 24) | ((networkBytes[1] & 0xFF) << 16) |
                       ((networkBytes[2] & 0xFF) << 8) | (networkBytes[3] & 0xFF);
      int addressInt = ((addressBytes[0] & 0xFF) << 24) | ((addressBytes[1] & 0xFF) << 16) |
                       ((addressBytes[2] & 0xFF) << 8) | (addressBytes[3] & 0xFF);

      return (networkInt & mask) == (addressInt & mask);
    } catch (UnknownHostException e) {
      return false;
    }
  }
}
