package com.api_questify.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.QuizOpcaoResponseDTO;
import com.api_questify.dto.QuizResponseDTO;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.exception.ConflictException;
import com.api_questify.exception.ResourceNotFoundException;
import com.api_questify.model.Desafio;
import com.api_questify.model.DesafioAgenda;
import com.api_questify.model.DesafioQuiz;
import com.api_questify.model.DesafioQuizOpcao;
import com.api_questify.repository.DesafioQuizOpcaoRepository;
import com.api_questify.repository.DesafioQuizRepository;
import com.api_questify.repository.DesafioRepository;
import com.api_questify.repository.ResultadoRepository;
import com.api_questify.util.UtilsGeral;

@Service
public class DesafioService {

    @Autowired
    private DesafioRepository repository;

    @Autowired
    private DesafioAgendaService agendaService;

    @Autowired
    private ResultadoRepository repositorioResultado;

    @Transactional(rollbackFor = Exception.class)
    public Desafio salvar(DesafioRequestDTO dto) {

        String hashPergunta = UtilsGeral.gerarHashPergunta(dto.getDsPergunta());

        if (repository.existsByDsHashPergunta(hashPergunta)) {
            throw new ConflictException("Ja existe um desafio com pergunta igual ou muito parecida");
        }

        Desafio objeto = new Desafio();
        objeto.setDsPergunta(dto.getDsPergunta());
        objeto.setDsHashPergunta(hashPergunta);
        objeto.setTpDificuldade(dto.getTpDificuldade());
        objeto.setTpDesafio(dto.getTpDesafio());
        objeto.setDsResposta(dto.getDsResposta());

        objeto = repository.save(objeto);

        agendaService.criarAgendaPorDesafio(dto, objeto);

        return objeto;
    }

    @Transactional(rollbackFor = Exception.class)
    public Desafio salvarQuiz(DesafioQuizRequestDTO dto) {

        String hashPergunta = UtilsGeral.gerarHashPergunta(dto.getDsPergunta());

        if (repository.existsByDsHashPergunta(hashPergunta)) {
            throw new ConflictException("Ja existe um desafio com pergunta igual ou muito parecida");
        }

        Desafio objeto = new Desafio();
        objeto.setDsPergunta(dto.getDsPergunta());
        objeto.setDsHashPergunta(hashPergunta);
        objeto.setTpDificuldade(dto.getTpDificuldade());
        objeto.setTpDesafio(dto.getTpDesafio());
        objeto.setDsResposta(dto.getDsResposta());

        objeto = repository.save(objeto);

        estruturaBodyPorTipo(dto, objeto);

        DesafioRequestDTO dtoPadrao = new DesafioRequestDTO();
        dtoPadrao.setDsPergunta(dto.getDsPergunta());
        dtoPadrao.setDsResposta(dto.getDsResposta());
        dtoPadrao.setDtFim(dto.getDtFim());
        dtoPadrao.setDtInicio(dto.getDtInicio());
        dtoPadrao.setTpDesafio(dto.getTpDesafio());
        dtoPadrao.setTpDificuldade(dto.getTpDificuldade());

        agendaService.criarAgendaPorDesafio(dtoPadrao, objeto);

        return objeto;
    }

    public boolean existePergunta(String pergunta) {
        String hashPergunta = UtilsGeral.gerarHashPergunta(pergunta);
        return hashPergunta != null && repository.existsByDsHashPergunta(hashPergunta);
    }

    private void validarConsistenciaQuiz(DesafioQuizRequestDTO dto) {

        var conteudo = dto.getConteudo();

        var opcaoCorreta = conteudo.getOpcoes().stream()
                .filter(o -> o.getCdOpcao().equals(conteudo.getDsRespostaCorreta()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Resposta correta inválida"));

        if (!opcaoCorreta.getNmRotulo().equals(dto.getDsResposta())) {
            throw new BusinessException("dsResposta não corresponde à opção correta");
        }
    }

    public void estruturaBodyPorTipo(DesafioQuizRequestDTO dto, Desafio objeto) {

        validarPorTipo(dto);

        switch (objeto.getTpDesafio()) {

            case QUIZ:
                salvarQuiz(dto, objeto);
                break;

            default:
                repository.save(objeto);
                break;
        }
    }

    // QUIZ
    private void salvarQuiz(DesafioQuizRequestDTO dto, Desafio desafio) {

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

        repository.save(desafio);
    }

    public void validarPorTipo(DesafioQuizRequestDTO dto) {

        switch (dto.getTpDesafio()) {

            case QUIZ:

                if (dto.getConteudo() == null) {
                    throw new RuntimeException("Conteúdo do quiz é obrigatório");
                }

                if (dto.getConteudo().getOpcoes() == null ||
                        dto.getConteudo().getOpcoes().isEmpty()) {

                    throw new RuntimeException("Quiz deve ter opções");
                }

                validarConsistenciaQuiz(dto);

                break;

            default:

                if (dto.getDsResposta() == null) {
                    throw new RuntimeException("Resposta é obrigatória");
                }

                break;
        }
    }

    public Desafio obterPorId(Long id) throws Exception {
        Optional<Desafio> objeto = repository.findById(id);
        if (!objeto.isPresent()) {
            throw new ResourceNotFoundException("Não existe desafio.");
        }
        return objeto.get();
    }

    public List<DesafioDiarioResponseDTO> obterDesafios() {
        List<Desafio> objetos = agendaService.obterDesafiosAtivos();
        List<DesafioDiarioResponseDTO> listResposta = new ArrayList<>();
        for (Desafio desafio : objetos) {
            DesafioDiarioResponseDTO dto = new DesafioDiarioResponseDTO();

            dto.setIdDesafio(desafio.getIdDesafio());
            dto.setDsPergunta(desafio.getDsPergunta());
            dto.setTpDesafio(desafio.getTpDesafio());
            dto.setTpDificuldade(desafio.getTpDificuldade());

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

            listResposta.add(dto);
        }
        return listResposta;
    }

    public List<Desafio> obterDesafiosAtivos() {
        List<Desafio> objetos = agendaService.obterDesafiosAtivos();
        return objetos;
    }

    public Desafio obterDesafioAtivoPorId(Long idDesafio) {
        LocalDate agora = LocalDate.now();
        Boolean ativo = agendaService.existeDesafioNoPeriodo(idDesafio, agora);
        if (!ativo) {
            throw new BusinessException("Desafio não está ativo no momento");
        }
        return repository.findById(idDesafio)
                .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));
    }


    
    @Transactional(rollbackFor = Exception.class)
    public void deletar(Long id) {
        agendaService.deletarRegistros(id);
        repositorioResultado.deleteByIdDesafio(id);
        repository.deleteById(id);
    }

}
