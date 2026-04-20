
package com.api_questify.interfaces;

import java.time.LocalDate;

import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;

public interface DesafioEstrategia {
    TipoDesafio getTipo();

    String gerarPrompt(String tema, LocalDate data, Dificuldade dificuldade);

    default void validar(DesafioRequestGeralDTO dto, LocalDate data) {

    }

    default void validarQuiz(DesafioQuizRequestDTO dto, LocalDate data) {

    }

}
