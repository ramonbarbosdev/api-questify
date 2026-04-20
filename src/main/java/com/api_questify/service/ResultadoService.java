package com.api_questify.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_questify.dto.RespostaDTO;
import com.api_questify.dto.ResultadoRequestDTO;
import com.api_questify.dto.ResultadoResponseDTO;
import com.api_questify.exception.BusinessException;
import com.api_questify.model.Desafio;
import com.api_questify.model.Resultado;
import com.api_questify.model.Usuario;
import com.api_questify.model.ValidacaoResultado;
import com.api_questify.repository.ResultadoRepository;
import com.api_questify.util.DesafioValidator;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository repository;
    @Autowired
    private DesafioService desafioService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DesafioValidator validator;

    public static final Integer MAXIMO_TENTATIVAS_PALAVRA = 5;

    @Transactional(rollbackFor = Exception.class)
    public ResultadoResponseDTO responderDesafio(ResultadoRequestDTO dto) {
        // 1. Obtenção de contexto
        Desafio desafio = desafioService.obterDesafioAtivoPorId(dto.getIdDesafio());
        Usuario usuario = obterOuSalvarDipositivo(dto.getIdDispositivo());
        
        if (usuario == null) return criarErroResponse("Usuário não identificado", desafio);

        // 2. Verificação de histórico
        Resultado existente = obterResultadoPorDesafioEDispositivo(dto.getIdDesafio(), dto.getIdDispositivo());

        // 3. Aplicação de Regras de Negócio (Travas)
        String mensagemErro = validarRegrasDeNegocio(existente, desafio);
        if (mensagemErro != null) return criarErroResponse(mensagemErro, desafio);

        // 4. Validação da Resposta técnica
        ValidacaoResultado validacao = validator.validar(dto.getDsResposta(), desafio);
        if (!validacao.isValido()) return criarErroResponse(validacao.getMensagem(), desafio);

        // 5. Processamento do Resultado
        Integer proximaTentativa = (existente != null ? existente.getNuTentativa() : 0) + 1;
        Resultado resultadoSalvo = persistirResposta(dto, usuario, validacao, proximaTentativa);

        // 6. Definição do estado de finalização para o Front-end
        boolean finalizado = verificarSeDesafioEncerrou(validacao.isSucesso(), proximaTentativa, desafio);

        return criarSucessoResponse(validacao, finalizado, desafio, resultadoSalvo.getDsResposta());
    }

    /**
     * Centraliza as verificações de permissão baseadas no tipo de desafio.
     */
    private String validarRegrasDeNegocio(Resultado existente, Desafio desafio) {
        if (existente == null) return null;

        // Regra universal: Se já acertou, não pode jogar de novo.
        if (existente.getFlSucesso()) return "Você já concluiu este desafio com sucesso!";

        // Regras por tipo
        return switch (desafio.getTpDesafio()) {
            case QUIZ -> "Você já respondeu este quiz. Apenas uma tentativa é permitida.";
            case PALAVRA -> (existente.getNuTentativa() >= MAXIMO_TENTATIVAS_PALAVRA) 
                            ? "Limite de tentativas atingido para este desafio." : null;
            default -> null;
        };
    }

    /**
     * Determina se o front-end deve encerrar a partida e ir para a tela de resultados.
     */
    private boolean verificarSeDesafioEncerrou(boolean sucesso, Integer tentativas, Desafio desafio) {
        return switch (desafio.getTpDesafio()) {
            case QUIZ -> true; // Quiz encerra no primeiro envio (acerto ou erro)
            case PALAVRA -> sucesso || tentativas >= MAXIMO_TENTATIVAS_PALAVRA;
            // case NUMERO, PADRAO -> sucesso;
            default -> sucesso;
        };
    }

    /**
     * Mapeia os dados para a entidade e salva no banco.
     */
    private Resultado persistirResposta(ResultadoRequestDTO dto, Usuario usuario, ValidacaoResultado v, Integer tentativa) {
        Resultado obj = new Resultado();
        obj.setIdDesafio(dto.getIdDesafio());
        obj.setIdUsuario(usuario.getIdUsuario());
        obj.setDsResposta(normalizar(dto.getDsResposta()));
        obj.setNuTentativa(tentativa);
        obj.setFlSucesso(v.isSucesso());
        obj.setDtConcluido(v.isSucesso() ? LocalDateTime.now() : null);
        obj.setTpStatus(v.getStatus());
        obj.setDsFeedback(serializarFeedback(v.getFeedback()));
        
        return repository.save(obj);
    }

    // --- MÉTODOS DE FORMATAÇÃO DE RESPOSTA (DTOs) ---

    private ResultadoResponseDTO criarErroResponse(String mensagem, Desafio desafio) {
        RespostaDTO resposta = new RespostaDTO();
        resposta.setValido(false);
        resposta.setMensagem(mensagem);
        
        return new ResultadoResponseDTO(false, false, desafio.getTpDesafio(), resposta);
    }

    private ResultadoResponseDTO criarSucessoResponse(ValidacaoResultado v, boolean finalizado, Desafio desafio, String respUsuario) {
        RespostaDTO resposta = new RespostaDTO();
        resposta.setValido(true);
        resposta.setStatus(v.getStatus());
        resposta.setFeedback(v.getFeedback());
        resposta.setRespostaUsuario(respUsuario);

        return new ResultadoResponseDTO(v.isSucesso(), finalizado, desafio.getTpDesafio(), resposta);
    }

    // --- MÉTODOS AUXILIARES ---

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) throw new BusinessException("Resposta é obrigatória");
        return valor.trim().toUpperCase();
    }

    private String serializarFeedback(List<String> feedback) {
        return (feedback == null || feedback.isEmpty()) ? null : String.join(",", feedback);
    }

    public Resultado obterResultadoPorDesafioEDispositivo(Long idDesafio, String idDispositivo) {
        return repository.findTopByIdDesafioAndUsuario_IdDispositivoOrderByIdResultadoDesc(idDesafio, idDispositivo)
                .orElse(null);
    }

    public Usuario obterOuSalvarDipositivo(String id) {
        Usuario usuario = usuarioService.obterUsuarioPorDispositivo(id);
        return (usuario != null) ? usuario : usuarioService.salvarDispositivo(id);
    }
}