package com.api_nivra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesafioComResultadoDTO {

    private Long idDesafio;
    private String dsPergunta;

    private boolean respondido;
    private ResultadoDTO resultado;

}