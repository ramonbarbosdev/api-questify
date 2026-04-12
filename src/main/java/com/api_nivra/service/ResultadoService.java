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
        validarTentativas(existente);

        Usuario usuario = obterOuSalvarDipositivo(dto.getIdDispositivo());

        if (usuario == null) {
            throw new BusinessException("Usuario não existe.");
        }

        // =========================
        // 🧠 VALIDAÇÃO CENTRAL
        // =========================
        ValidacaoResultado resultadoValidacao = validator.validar(dto.getDsResposta(), desafio);

        if (!resultadoValidacao.isValido()) {
            throw new BusinessException(resultadoValidacao.getMensagem());
        }

        Integer nuTentativas = existente != null
                ? existente.getNuTentativa() + 1
                : 1;

        // =========================
        // 💾 SALVAR
        // =========================
        Resultado objeto = new Resultado();
        objeto.setIdDesafio(dto.getIdDesafio());
        objeto.setIdUsuario(usuario.getIdUsuario());
        objeto.setDsResposta(normalizar(dto.getDsResposta()));
        objeto.setNuTentativa(nuTentativas);

        objeto.setFlSucesso(resultadoValidacao.isSucesso());
        objeto.setDtConcluido(
                resultadoValidacao.isSucesso() ? LocalDateTime.now() : null);

        // 🔥 NOVO (IMPORTANTE)
        objeto.setTpStatus(resultadoValidacao.getStatus());

        repository.save(objeto);

        // =========================
        // 📤 RESPOSTA PADRÃO
        // =========================
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("valido", true);
        resposta.put("status", resultadoValidacao.getStatus());
        resposta.put("feedback", resultadoValidacao.getFeedback());

        return new ResultadoResponseDTO(
                resultadoValidacao.isSucesso(),
                desafio.getTpDesafio(),
                resposta);
    }

    private void validarTentativas(Resultado objeto) {

        if (objeto == null)
            return;

        if (objeto.getNuTentativa() >= MAXIMO_TENTATIVA) {
            throw new BusinessException("Limite de tentativas atingido.");
        }

        if (objeto.getFlSucesso()) {
            throw new BusinessException("Você já respondeu o desafio.");
        }
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
}