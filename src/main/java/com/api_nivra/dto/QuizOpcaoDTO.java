package com.api_nivra.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class QuizOpcaoDTO {

    @NotBlank(message = "Código da opção obrigatório")
    private String cdOpcao;

    @NotBlank(message = "Label obrigatório")
    @Size(max = 100, message = "Label muito longo")
    private String nmRotulo;
}