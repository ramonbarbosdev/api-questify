package com.api_nivra.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api_nivra.enums.Dificuldade;
import com.api_nivra.enums.TipoDesafio;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesafioQuizRequestDTO extends DesafioBaseDTO {


    
    @NotNull(message = "O tipo de desafio é obrigatorio!")
     @Schema(defaultValue = "QUIZ", example = "QUIZ")
    private TipoDesafio tpDesafio ;

    @NotNull(message = "Conteúdo do quiz é obrigatório")
    private QuizConteudoDTO conteudo;

}
