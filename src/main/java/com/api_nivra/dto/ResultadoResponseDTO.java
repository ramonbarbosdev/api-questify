package com.api_nivra.dto;

import java.util.List;

import com.api_nivra.enums.TipoDesafio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoResponseDTO {

    private Boolean sucesso;
    private TipoDesafio tipo;
    private Object resposta;
}
