package com.api_nivra.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api_nivra.enums.Dificuldade;
import com.api_nivra.enums.TipoDesafio;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesafioRequestDTO {

    @NotNull(message = "A pergunta é obrigatorio!")
    private String dsPergunta;

    @NotNull(message = "A resposta é obrigatorio!")
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
