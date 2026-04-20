package com.api_questify.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_questify.dto.DesafioComResultadoDTO;
import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.dto.QuizOpcaoResponseDTO;
import com.api_questify.dto.QuizResponseDTO;
import com.api_questify.dto.ResultadoDTO;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.exception.ConflictException;
import com.api_questify.exception.ResourceNotFoundException;
import com.api_questify.model.Desafio;
import com.api_questify.model.DesafioAgenda;
import com.api_questify.model.DesafioQuiz;
import com.api_questify.model.DesafioQuizOpcao;
import com.api_questify.model.Resultado;
import com.api_questify.repository.DesafioQuizOpcaoRepository;
import com.api_questify.repository.DesafioQuizRepository;
import com.api_questify.repository.DesafioRepository;
import com.api_questify.repository.ResultadoRepository;
import com.api_questify.util.UtilsGeral;

@Service
public class DesafioService {

    private DesafioRepository repository;
    private DesafioAgendaService agendaService;
    private ResultadoRepository repositorioResultado;
    private QuizFacadeService quizFacadeService;
    public static final Integer MAXIMO_TENTATIVA = 5;

    public DesafioService(DesafioRepository repository, DesafioAgendaService agendaService,
            ResultadoRepository repositorioResultado, QuizFacadeService quizFacadeService) {
        this.repository = repository;
        this.agendaService = agendaService;
        this.repositorioResultado = repositorioResultado;
        this.quizFacadeService = quizFacadeService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Desafio salvar(DesafioRequestGeralDTO dto) {

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
        objeto.setNuTamanhoResposta(dto.getDsResposta().length());

        objeto = repository.save(objeto);

        estruturaBodyPorTipo(dto, objeto);

        agendaService.criarAgendaPorDesafio(dto, objeto);

        return objeto;
    }

    public void estruturaBodyPorTipo(DesafioRequestGeralDTO dto, Desafio objeto) {

        validarPorTipo(dto);

        switch (objeto.getTpDesafio()) {

            case QUIZ:
                objeto = quizFacadeService.salvarQuiz(dto, objeto);
                repository.save(objeto);
                break;

            default:
                repository.save(objeto);
                break;
        }
    }

    public void validarPorTipo(DesafioRequestGeralDTO dto) {

        switch (dto.getTpDesafio()) {

            case QUIZ:
                quizFacadeService.validarQuiz(dto);
                break;

            default:
                if (dto.getDsResposta() == null) {
                    throw new RuntimeException("Resposta é obrigatória");
                }
                break;
        }
    }

    public boolean existePergunta(String pergunta) {
        String hashPergunta = UtilsGeral.gerarHashPergunta(pergunta);
        return hashPergunta != null && repository.existsByDsHashPergunta(hashPergunta);
    }

    public List<DesafioComResultadoDTO> obterDesafiosComResultado(String idDispositivo) {
        List<Desafio> desafios = obterDesafiosAtivos();

        return desafios.stream()
                .map(desafio -> montarDesafioDTO(desafio, idDispositivo))
                .toList();
    }

    private DesafioComResultadoDTO montarDesafioDTO(Desafio desafio, String idDispositivo) {
        DesafioComResultadoDTO dto = new DesafioComResultadoDTO();

        dto.setIdDesafio(desafio.getIdDesafio());
        dto.setDsPergunta(desafio.getDsPergunta());
        dto.setTpDesafio(desafio.getTpDesafio());
        dto.setNuTamanhoResposta(desafio.getNuTamanhoResposta());
        dto.setTpDificuldade(desafio.getTpDificuldade());

        dto.setNuMaximoTentativa(TipoDesafio.QUIZ.equals(desafio.getTpDesafio()) ? 1 : MAXIMO_TENTATIVA);

        quizFacadeService.obterDesafiosComQuiz(dto, desafio);

        List<Resultado> tentativas = repositorioResultado
                .buscarPorDesafioEdispositivo(desafio.getIdDesafio(), idDispositivo);

        if (tentativas.isEmpty()) {
            dto.setFlFinalizado(false);
            return dto;
        }

        dto.setResultado(mapearResultadoDTO(tentativas));
        dto.setFlFinalizado(verificarSeFinalizado(desafio, tentativas));

        return dto;
    }

    private ResultadoDTO mapearResultadoDTO(List<Resultado> tentativas) {
        ResultadoDTO rdto = new ResultadoDTO();

        rdto.setRespostas(tentativas.stream().map(Resultado::getDsResposta).toList());
        rdto.setTentativas(tentativas.stream().map(Resultado::getTpStatus).toList());
        rdto.setFeedbacks(tentativas.stream()
                .map(r -> UtilsGeral.desserializarFeedback(r.getDsFeedback()))
                .toList());

        boolean sucesso = tentativas.stream()
                .anyMatch(r -> Boolean.TRUE.equals(r.getFlSucesso()));
        rdto.setSucesso(sucesso);

        return rdto;
    }

    private boolean verificarSeFinalizado(Desafio desafio, List<Resultado> tentativas) {
        boolean sucesso = tentativas.stream().anyMatch(r -> Boolean.TRUE.equals(r.getFlSucesso()));

        if (TipoDesafio.QUIZ.equals(desafio.getTpDesafio())) {
            return !tentativas.isEmpty(); 
        }

        return sucesso || tentativas.size() >= MAXIMO_TENTATIVA;
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
            dto.setNuTamanhoResposta(desafio.getNuTamanhoResposta());
            dto.setNuMaximoTentativa(MAXIMO_TENTATIVA);

            quizFacadeService.obterDesafiosComQuiz(dto, desafio);

            listResposta.add(dto);
        }
        return listResposta;
    }

    public Desafio obterPorId(Long id) throws Exception {
        Optional<Desafio> objeto = repository.findById(id);
        if (!objeto.isPresent()) {
            throw new ResourceNotFoundException("Não existe desafio.");
        }
        return objeto.get();
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
