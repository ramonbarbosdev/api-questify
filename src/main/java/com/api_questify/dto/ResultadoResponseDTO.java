package com.api_questify.dto;

import com.api_questify.enums.TipoDesafio;

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
    private Boolean flFinalizado;
    private TipoDesafio tpDesafio;
    private RespostaDTO resposta;

}
