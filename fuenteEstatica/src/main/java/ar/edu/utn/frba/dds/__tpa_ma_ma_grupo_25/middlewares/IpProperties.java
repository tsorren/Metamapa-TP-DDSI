package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.middlewares;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class IpProperties {
  private List<String> whitelist;
  private List<String> blacklist;

  public IpProperties() {
    this.whitelist = new ArrayList<String>();
    this.blacklist = new ArrayList<String>();
    this.whitelist.add("127.0.0.1");
  }

  public void agregarAWhitelist(String ip) {
    this.whitelist.add(ip);
  }

  public void agregarABlacklist(String ip) {
    this.blacklist.add(ip);
  }
}