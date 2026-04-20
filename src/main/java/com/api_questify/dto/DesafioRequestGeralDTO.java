package com.api_questify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesafioRequestGeralDTO extends DesafioBaseDTO {

    private QuizConteudoDTO conteudo;
}
