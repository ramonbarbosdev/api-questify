package com.api_questify.dto;

import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DesafioBaseResponseDTO {
    private Long idDesafio;
    private String dsPergunta;
    private Boolean flFinalizado;
    private TipoDesafio tpDesafio;
    private Dificuldade tpDificuldade;
    private Integer nuTamanhoResposta;
    private Integer nuMaximoTentativa;

}
