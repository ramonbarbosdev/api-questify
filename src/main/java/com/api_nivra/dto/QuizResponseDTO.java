package com.api_nivra.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {

    private Boolean flEmbaralhar;
    private Integer nuTempoLimite;

    private List<QuizOpcaoResponseDTO> opcoes;
}