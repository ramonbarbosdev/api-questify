package com.api_questify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioComResultadoDTO;
import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.dto.QuizOpcaoResponseDTO;
import com.api_questify.dto.QuizResponseDTO;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.model.Desafio;
import com.api_questify.model.DesafioQuiz;
import com.api_questify.model.DesafioQuizOpcao;

@Service
public class QuizFacadeService {

    public Desafio salvarQuiz(DesafioRequestGeralDTO dto, Desafio desafio) {

        var conteudo = dto.getConteudo();

        if (conteudo == null || conteudo.getOpcoes() == null || conteudo.getOpcoes().isEmpty()) {
            throw new RuntimeException("Conteúdo do quiz inválido");
        }

        DesafioQuiz quiz = new DesafioQuiz();
        quiz.setFlEmbaralhar(conteudo.getFlEmbaralhar());
        quiz.setNuTempoLimite(conteudo.getNuTempoLimite());
        quiz.setDsRespostaCorreta(conteudo.getDsRespostaCorreta());

        desafio.setConteudo(quiz);

        List<DesafioQuizOpcao> opcoes = new ArrayList<>();

        for (var opcaoDto : conteudo.getOpcoes()) {

            DesafioQuizOpcao opcao = new DesafioQuizOpcao();
            opcao.setQuiz(quiz);
            opcao.setCdCodigo(opcaoDto.getCdOpcao());
            opcao.setNmRotulo(opcaoDto.getNmRotulo());

            opcoes.add(opcao);
        }

        quiz.setOpcoes(opcoes);

        var opcaoCorreta = opcoes.stream()
                .filter(o -> o.getCdCodigo().equals(conteudo.getDsRespostaCorreta()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resposta correta inválida"));

        desafio.setDsResposta(opcaoCorreta.getNmRotulo());

        return desafio;
    }

    public void validarQuiz(DesafioRequestGeralDTO dto) {
        if (dto.getConteudo() == null) {
            throw new RuntimeException("Conteúdo do quiz é obrigatório");
        }
        if (dto.getConteudo().getOpcoes() == null ||
                dto.getConteudo().getOpcoes().isEmpty()) {

            throw new RuntimeException("Quiz deve ter opções");
        }
        validarConsistenciaQuiz(dto);
    }

    private void validarConsistenciaQuiz(DesafioRequestGeralDTO dto) {

        var conteudo = dto.getConteudo();

        var opcaoCorreta = conteudo.getOpcoes().stream()
                .filter(o -> o.getCdOpcao().equals(conteudo.getDsRespostaCorreta()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resposta correta inválida"));

        if (!opcaoCorreta.getNmRotulo().equals(dto.getDsResposta())) {
            throw new BusinessException("dsResposta não corresponde à opção correta");
        }
    }

    public void obterDesafiosComQuiz(DesafioDiarioResponseDTO dto, Desafio desafio) {

        if (desafio.getTpDesafio() == TipoDesafio.QUIZ && desafio.getConteudo() != null) {

            DesafioQuiz quiz = desafio.getConteudo();

            QuizResponseDTO conteudo = new QuizResponseDTO();
            conteudo.setFlEmbaralhar(quiz.getFlEmbaralhar());
            conteudo.setNuTempoLimite(quiz.getNuTempoLimite());

            List<QuizOpcaoResponseDTO> opcoes = quiz.getOpcoes().stream()
                    .map(o -> new QuizOpcaoResponseDTO(
                            o.getCdCodigo(),
                            o.getNmRotulo()))
                    .toList();

            conteudo.setOpcoes(opcoes);

            dto.setConteudo(conteudo);
        }
    }

    public void obterDesafiosComQuiz(DesafioComResultadoDTO dto, Desafio desafio) {

        if (desafio.getTpDesafio() == TipoDesafio.QUIZ && desafio.getConteudo() != null) {

            DesafioQuiz quiz = desafio.getConteudo();

            QuizResponseDTO conteudo = new QuizResponseDTO();
            conteudo.setFlEmbaralhar(quiz.getFlEmbaralhar());
            conteudo.setNuTempoLimite(quiz.getNuTempoLimite());

            List<QuizOpcaoResponseDTO> opcoes = quiz.getOpcoes().stream()
                    .map(o -> new QuizOpcaoResponseDTO(
                            o.getCdCodigo(),
                            o.getNmRotulo()))
                    .toList();

            conteudo.setOpcoes(opcoes);

            dto.setConteudo(conteudo);
        }
    }

}
