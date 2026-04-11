package com.api_nivra.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoRequestDTO {
    
    @NotNull(message = "o desafio é obrigatorio!")
    private Long idDesafio;

    @NotNull(message = "O dispositvo é obrigatorio!")
    private String idDispositivo;

    @NotNull(message = "A resposta é obrigatorio!")
    private String dsResposta;
}
