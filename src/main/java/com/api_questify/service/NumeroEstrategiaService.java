package com.api_questify.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.interfaces.DesafioEstrategia;

@Service
public class NumeroEstrategiaService implements DesafioEstrategia {

    @Override
    public TipoDesafio getTipo() {
        return TipoDesafio.NUMERO;
    }

    @Override
    public String gerarPrompt(String tema, LocalDate data, Dificuldade dificuldade) {

        String today = data.toString();

        return """
                Gere um problema matemático criativo.
                Nível: %s.

                REQUISITOS POR NÍVEL:
                - FACIL: Aritmética simples (soma/subtração/multiplicação básica).
                - MODERADO: Equações de 1º grau ou lógica um pouco mais complexa.
                - DIFICIL: Problemas de lógica, porcentagem composta ou pequenos desafios de raciocínio.

                RETORNO JSON:
                {
                  "dsPergunta": "Enunciado do problema",
                  "dsResposta": "Apenas o número da resposta",
                  "tpDificuldade": "%s",
                  "tpDesafio": "NUMERO",
                  "dtInicio": "%s",
                  "dtFim": "%s"
                }

                REGRAS:
                - A resposta deve ser obrigatoriamente um número INTEIRO positivo.
                - Não inclua unidades (ex: use "10" em vez de "10 metros").
                """.formatted(dificuldade.name(), dificuldade.name(), today, today);
    }

    @Override
    public void validar(DesafioRequestGeralDTO dto, LocalDate data) {
        if (!dto.getDsResposta().matches("\\d+")) {
            throw new BusinessException("Resposta nao e numero");
        }
    }
}
