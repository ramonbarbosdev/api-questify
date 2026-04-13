package com.api_nivra.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_nivra.dto.DesafioComResultadoDTO;
import com.api_nivra.dto.DesafioDiarioResponseDTO;
import com.api_nivra.dto.RespostaDTO;
import com.api_nivra.dto.ResultadoDTO;
import com.api_nivra.dto.ResultadoRequestDTO;
import com.api_nivra.dto.ResultadoResponseDTO;
import com.api_nivra.enums.TipoDesafio;
import com.api_nivra.exception.BusinessException;
import com.api_nivra.model.Desafio;
import com.api_nivra.model.Resultado;
import com.api_nivra.model.Usuario;
import com.api_nivra.model.ValidacaoResultado;
import com.api_nivra.repository.ResultadoRepository;
import com.api_nivra.util.DesafioValidator;

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

    public static final Integer MAXIMO_TENTATIVA = 5;

    @Transactional(rollbackFor = Exception.class)
    public ResultadoResponseDTO responderDesafio(ResultadoRequestDTO dto) {

        Desafio desafio = desafioService.obterDesafioAtivoPorId(dto.getIdDesafio());

        Resultado existente = obterResultadoPorDesafio(dto.getIdDesafio());

        if (existente != null && existente.getFlSucesso()) {
            return erro("Você já respondeu o desafio.", desafio);
        }

        Usuario usuario = obterOuSalvarDipositivo(dto.getIdDispositivo());

        if (usuario == null) {
            return erro("Usuário não existe", desafio);
        }

        ValidacaoResultado validacao = validator.validar(dto.getDsResposta(), desafio);

        if (!validacao.isValido()) {
            return erro(validacao.getMensagem(), desafio);
        }

        Integer nuTentativas = existente != null
                ? existente.getNuTentativa() + 1
                : 1;

        if (nuTentativas > MAXIMO_TENTATIVA) {
            return erro("Limite de tentativas atingido", desafio);
        }

        boolean esgotouTentativas = nuTentativas >= MAXIMO_TENTATIVA;

        boolean finalizado = esgotouTentativas;

        Resultado obj = new Resultado();
        obj.setIdDesafio(dto.getIdDesafio());
        obj.setIdUsuario(usuario.getIdUsuario());
        obj.setDsResposta(normalizar(dto.getDsResposta()));
        obj.setNuTentativa(nuTentativas);
        obj.setFlSucesso(validacao.isSucesso());
        obj.setDtConcluido(validacao.isSucesso() ? LocalDateTime.now() : null);
        obj.setTpStatus(validacao.getStatus());
        obj.setDsFeedback(serializarFeedback(validacao.getFeedback()));

        repository.save(obj);

        finalizado = validacao.isSucesso() || esgotouTentativas;

        return sucesso(validacao, finalizado, desafio, obj.getDsResposta());
    }

    private ResultadoResponseDTO erro(String mensagem, Desafio desafio) {
        RespostaDTO resposta = new RespostaDTO();
        resposta.setValido(false);
        resposta.setMensagem(mensagem);
        resposta.setStatus(null);
        resposta.setFeedback(null);

        return new ResultadoResponseDTO(
                false,
                false,
                desafio.getTpDesafio(),
                resposta);
    }

    private ResultadoResponseDTO sucesso(
            ValidacaoResultado validacao,
            boolean finalizado,
            Desafio desafio,
            String respostaUsuario
        ) {

        RespostaDTO resposta = new RespostaDTO();
        resposta.setValido(true);
        resposta.setMensagem(null);
        resposta.setStatus(validacao.getStatus());
        resposta.setFeedback(validacao.getFeedback());
        resposta.setRespostaUsuario(respostaUsuario);

        return new ResultadoResponseDTO(
                validacao.isSucesso(),
                finalizado,
                desafio.getTpDesafio(),
                resposta);
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new BusinessException("Resposta é obrigatória");
        }
        return valor.trim().toUpperCase();
    }

    public Resultado obterResultadoPorDesafio(Long idDesafio) {

        return repository
                .findTopByIdDesafioOrderByIdResultadoDesc(idDesafio)
                .orElse(null);
    }

    public Usuario obterOuSalvarDipositivo(String id) {

        Usuario usuario = usuarioService.obterUsuarioPorDispositivo(id);

        if (usuario == null) {
            usuario = usuarioService.salvarDispositivo(id);
        }

        return usuario;
    }

    private String serializarFeedback(List<String> feedback) {
        if (feedback == null)
            return null;
        return String.join(",", feedback);
    }

   
}