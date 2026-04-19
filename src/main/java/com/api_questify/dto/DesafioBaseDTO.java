package com.api_questify.dto;

import java.time.LocalDate;

import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DesafioBaseDTO {

    @NotNull(message = "A pergunta é obrigatorio!")
    private String dsPergunta;

    @NotBlank(message = "A resposta é obrigatorio!")
    private String dsResposta;

    @NotNull(message = "O tipo de dificuldade é obrigatorio!")
    private Dificuldade tpDificuldade;

    @NotNull(message = "O tipo de desafio é obrigatorio!")
    private TipoDesafio tpDesafio;

    @NotNull(message = "A data de inicio é obrigatorio!")
    private LocalDate dtInicio;

    @NotNull(message = "A data do fim é obrigatorio!")
    private LocalDate dtFim;

}