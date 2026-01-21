package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
}