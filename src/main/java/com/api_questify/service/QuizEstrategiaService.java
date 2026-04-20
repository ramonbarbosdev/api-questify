package com.api_questify.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.interfaces.DesafioEstrategia;

@Service
public class QuizEstrategiaService implements DesafioEstrategia {

    @Override
    public TipoDesafio getTipo() {
        return TipoDesafio.QUIZ;

    }

    @Override
    public String gerarPrompt(String tema, LocalDate data, Dificuldade dificuldade) {

        String today = data.toString();

        String prompt = """
                Crie um desafio de múltipla escolha (QUIZ) sobre o tema: %s.
                Nível de dificuldade: %s.

                ESTRUTURA JSON OBRIGATÓRIA:
                {
                  "dsPergunta": "Sua pergunta clara aqui",
                  "dsResposta": "O texto exato da alternativa correta",
                  "tpDificuldade": "%s",
                  "tpDesafio": "QUIZ",
                  "dtInicio": "%s",
                  "dtFim": "%s",
                  "conteudo": {
                    "opcoes": [
                      { "cdOpcao": "A", "nmRotulo": "Texto da opção A" },
                      { "cdOpcao": "B", "nmRotulo": "Texto da opção B" },
                      { "cdOpcao": "C", "nmRotulo": "Texto da opção C" },
                      { "cdOpcao": "D", "nmRotulo": "Texto da opção D" }
                    ],
                    "dsRespostaCorreta": "Letra da opção correta (A, B, C ou D)",
                    "flEmbaralhar": true,
                    "nuTempoLimite": 30
                  }
                }

                REGRAS CRÍTICAS:
                1. O campo "dsResposta" DEVE ser idêntico ao "nmRotulo" da alternativa indicada em "dsRespostaCorreta".
                2. Crie 1 alternativa correta e 3 distratores (opções incorretas) que sejam plausíveis, evitando itens óbvios demais.
                3. Não use markdown (como ```json). Retorne apenas o objeto puro.
                4. Idioma: Português Brasileiro.
                """
                .formatted(tema, dificuldade.name(), dificuldade.name(), today, today);

        return prompt;
    }

    @Override
    public void validarQuiz(DesafioQuizRequestDTO dto, LocalDate data) {

        if (dto.getTpDesafio() != TipoDesafio.QUIZ) {
            throw new BusinessException("Tipo nao e QUIZ");
        }

        if (dto.getConteudo() == null) {
            throw new BusinessException("Conteudo nao informado");
        }

        var conteudo = dto.getConteudo();

        if (conteudo.getOpcoes() == null || conteudo.getOpcoes().size() != 4) {
            throw new BusinessException("Deve conter 4 opcoes");
        }

        Set<String> esperados = Set.of("A", "B", "C", "D");

        Set<String> recebidos = conteudo.getOpcoes()
                .stream()
                .map(o -> o.getCdOpcao())
                .collect(Collectors.toSet());

        if (!recebidos.equals(esperados)) {
            throw new BusinessException("Opcoes devem ser A, B, C e D");
        }

        Set<String> textos = conteudo.getOpcoes()
                .stream()
                .map(o -> o.getNmRotulo())
                .collect(Collectors.toSet());

        if (textos.size() != 4) {
            throw new BusinessException("Alternativas duplicadas");
        }

        String letraCorreta = conteudo.getDsRespostaCorreta();

        if (!esperados.contains(letraCorreta)) {
            throw new BusinessException("Resposta correta invalida");
        }

        String textoCorreto = conteudo.getOpcoes()
                .stream()
                .filter(o -> o.getCdOpcao().equals(letraCorreta))
                .findFirst()
                .map(o -> o.getNmRotulo())
                .orElseThrow(() -> new BusinessException("Opcao correta nao encontrada"));

        if (!textoCorreto.equalsIgnoreCase(dto.getDsResposta())) {
            throw new BusinessException("dsResposta nao bate com alternativa correta");
        }

        conteudo.setFlEmbaralhar(true);
        conteudo.setNuTempoLimite(getTempoPorDificuldade(dto.getTpDificuldade()));
    }

    private int getTempoPorDificuldade(Dificuldade dificuldade) {
        return switch (dificuldade) {
            case FACIL -> 60;
            case MODERADO -> 45;
            case DIFICIL -> 30;
        };
    }

}
