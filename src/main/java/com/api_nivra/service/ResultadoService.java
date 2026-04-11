package com.api_nivra.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_nivra.dto.ResultadoRequestDTO;
import com.api_nivra.dto.ResultadoResponseDTO;
import com.api_nivra.enums.TipoDesafio;
import com.api_nivra.exception.BusinessException;
import com.api_nivra.model.Desafio;
import com.api_nivra.model.Resultado;
import com.api_nivra.model.Usuario;
import com.api_nivra.repository.ResultadoRepository;

@Service
public class ResultadoService {

    @Autowired
    private ResultadoRepository repository;

    @Autowired
    private DesafioService desafioService;

    @Autowired
    private UsuarioService usuarioService;

    public static final Integer MAXIMO_TENTATIVA = 5;

    @Transactional(rollbackFor = Exception.class)
    public ResultadoResponseDTO responderDesafio(ResultadoRequestDTO dto) {

        if (dto.getDsResposta() == null || dto.getDsResposta().isBlank()) {
            throw new BusinessException("Resposta é obrigatória");
        }

        Desafio desafio = desafioService.obterDesafioAtivoPorId(dto.getIdDesafio());

        Resultado existente = obterResultadoPorDesafio(dto.getIdDesafio());
        validarTentativas(existente);

        String respostaUsuario = normalizar(dto.getDsResposta());
        String respostaCorreta = normalizar(desafio.getDsResposta());

        Boolean flSucesso = calcularSucesso(
                respostaUsuario,
                respostaCorreta,
                desafio.getTpDesafio()
        );

        Integer nuTentativas = existente != null
                ? existente.getNuTentativa() + 1
                : 1;

        Usuario usuario = obterOuSalvarDipositivo(dto.getIdDispositivo());

        Resultado objeto = new Resultado();
        objeto.setIdDesafio(dto.getIdDesafio());
        objeto.setDtConcluido(flSucesso ? LocalDateTime.now() : null);
        objeto.setFlSucesso(flSucesso);
        objeto.setNuTentativa(nuTentativas);
        objeto.setIdUsuario(usuario.getIdUsuario());
        objeto.setDsResposta(respostaUsuario); 

        repository.save(objeto);

        return criarObjetoResposta(objeto, desafio);
    }

    private void validarTentativas(Resultado objeto) {

        if (objeto == null) return;

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

    private int parseNumero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            throw new BusinessException("Resposta deve ser um número");
        }
    }

    private boolean calcularSucesso(
            String respostaUsuario,
            String respostaCorreta,
            TipoDesafio tipo) {

        switch (tipo) {

            case PALAVRA:
                return respostaCorreta.equals(respostaUsuario);

            case NUMERO:
                return parseNumero(respostaUsuario) == parseNumero(respostaCorreta);

            case QUIZ:
                return respostaCorreta.equalsIgnoreCase(respostaUsuario);

            case PADRAO:
                return respostaCorreta.equals(respostaUsuario);

            default:
                throw new IllegalArgumentException("Tipo de desafio não suportado");
        }
    }

    private ResultadoResponseDTO criarObjetoResposta(Resultado objeto, Desafio desafio) {

        Object resultado = gerarResposta(
                desafio.getDsResposta(),
                objeto.getDsResposta(),
                desafio.getTpDesafio()
        );

        return new ResultadoResponseDTO(
                objeto.getFlSucesso(),
                desafio.getTpDesafio(),
                objeto.getFlSucesso() ? resultado : null 
        );
    }

    private Object gerarResposta(
            String respostaCorreta,
            String respostaUsuario,
            TipoDesafio tipo) {

        respostaUsuario = normalizar(respostaUsuario);
        respostaCorreta = normalizar(respostaCorreta);

        switch (tipo) {

            case PALAVRA:

                if (respostaUsuario.length() != respostaCorreta.length()) {
                    throw new BusinessException("Resposta inválida");
                }

                if (!respostaUsuario.matches("[A-Z]+")) {
                    throw new BusinessException("Resposta contém caracteres inválidos");
                }

                return gerarFeedbackPalavra(respostaUsuario, respostaCorreta);

            case NUMERO:

                int guess = parseNumero(respostaUsuario);
                int answer = parseNumero(respostaCorreta);

                if (guess > answer) return "BAIXO";
                if (guess < answer) return "ALTO";
                return "CORRETO";

            case QUIZ:
                return Map.of(
                        "respostaCorreta", respostaCorreta,
                        "selecionada", respostaUsuario
                );

            default:
                throw new IllegalArgumentException("Tipo de desafio não suportado");
        }
    }

    private List<String> gerarFeedbackPalavra(String guess, String answer) {

        List<String> resultado = new ArrayList<>();

        for (int i = 0; i < guess.length(); i++) {

            char g = guess.charAt(i);
            char a = answer.charAt(i);

            if (g == a) {
                resultado.add("CORRETA");
            } else if (answer.indexOf(g) >= 0) {
                resultado.add("FECHADA");
            } else {
                resultado.add("ERRADA");
            }
        }

        return resultado;
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