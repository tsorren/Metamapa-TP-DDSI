package ar.edu.utn.frba.dds.__tpa_ma_ma_grupo_25.models.dtos.input.desastresApi;

import lombok.Data;

import java.util.List;

@Data
public class DataResponseInput {
  private int current_page;
  private int last_page;
  private List<HechoExternoInput> data;
}
