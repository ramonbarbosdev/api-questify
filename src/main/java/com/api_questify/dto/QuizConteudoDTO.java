package com.api_questify.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizConteudoDTO {

    @NotNull(message = "As opções são obrigatórias")
    @Size(min = 2, max = 6, message = "Quiz deve ter entre 2 e 6 opções")
    private List<@Valid QuizOpcaoDTO> opcoes;

    @NotBlank(message = "Resposta correta é obrigatória")
    @Schema(defaultValue = "A", example = "A")
    private String dsRespostaCorreta;

    private Boolean flEmbaralhar;

    @Min(value = 1000, message = "Tempo mínimo é 1 segundo")
    @Max(value = 60000, message = "Tempo máximo é 60 segundos")
    private Integer nuTempoLimite;
}