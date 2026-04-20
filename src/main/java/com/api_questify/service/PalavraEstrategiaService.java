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
public class PalavraEstrategiaService implements DesafioEstrategia {

    @Override
    public TipoDesafio getTipo() {
        return TipoDesafio.PALAVRA;
    }

    @Override
    public String gerarPrompt(String tema, LocalDate data, Dificuldade dificuldade) {

        int tamanho = getTamanhoPorDificuldade(dificuldade);
        String today = data.toString();

        String prompt = """
                Gere um desafio de adivinhação de palavra sobre o tema: %s.
                A palavra deve ter EXATAMENTE %d letras.

                PROCESSO INTERNO:
                1. Escolha um substantivo comum sobre o tema.
                2. Conte as letras. Se não tiver %d letras, escolha outra.
                3. Remova acentos e espaços.

                RETORNO JSON:
                {
                  "dsPergunta": "Dica ou definição da palavra",
                  "dsResposta": "PALAVRA_SEM_ACENTO",
                  "tpDificuldade": "%s",
                  "tpDesafio": "PALAVRA",
                  "dtInicio": "%s",
                  "dtFim": "%s"
                }

                REGRAS:
                - Resposta com EXATAMENTE %d letras.
                - Use apenas letras (A-Z), sem espaços, sem hífens e sem acentuação.
                - Não use verbos ou termos técnicos obscuros.
                - A pergunta deve ser instigante mas direta.
                """.formatted(tema, tamanho, tamanho, dificuldade.name(), today, today, tamanho);

        return prompt;

    }

    @Override
    public void validar(DesafioRequestGeralDTO dto, LocalDate data) {
        if (dto == null) {
            throw new BusinessException("IA nao retornou desafio");
        }

        if (dto.getDsPergunta() == null || dto.getDsPergunta().trim().isEmpty()) {
            throw new BusinessException("IA retornou pergunta vazia");
        }

        if (dto.getDsResposta() == null || dto.getDsResposta().trim().isEmpty()) {
            throw new BusinessException("IA retornou resposta vazia");
        }

        if (dto.getTpDificuldade() == null) {
            dto.setTpDificuldade(Dificuldade.FACIL);
        }

        if (dto.getTpDesafio() == null) {
            dto.setTpDesafio(TipoDesafio.PALAVRA);
        }

        if (dto.getTpDesafio() != TipoDesafio.PALAVRA) {
            throw new BusinessException("IA retornou tipo de desafio diferente de PALAVRA");
        }

        dto.setDtInicio(data);
        dto.setDtFim(data);
    }

    private int getTamanhoPorDificuldade(Dificuldade dificuldade) {
        return switch (dificuldade) {
            case FACIL -> 5;
            case MODERADO -> 7;
            case DIFICIL -> 10;
        };
    }
}
