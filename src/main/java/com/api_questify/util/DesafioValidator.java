package com.api_questify.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.api_questify.enums.TipoDesafio;
import com.api_questify.model.Desafio;
import com.api_questify.model.ValidacaoResultado;

@Component
public class DesafioValidator {

    public ValidacaoResultado validar(
            String respostaUsuario,
            Desafio desafio) {

        TipoDesafio tipo = desafio.getTpDesafio();

        return switch (tipo) {
            case PALAVRA -> validarPalavra(respostaUsuario, desafio);
            case NUMERO -> validarNumero(respostaUsuario, desafio);
            case QUIZ -> validarQuiz(respostaUsuario, desafio);
            case PADRAO -> validarPadrao(respostaUsuario, desafio);
        };
    }

    private ValidacaoResultado validarPalavra(String resposta, Desafio desafio) {

        String correta = desafio.getDsResposta().toUpperCase();
        String usuario = resposta.toUpperCase();

        if (!usuario.matches("[A-Z]+")) {
            return erro("Use apenas letras");
        }

        if (usuario.length() != correta.length()) {
            return erro("Tamanho inválido");
        }

        List<String> feedback = new ArrayList<>();

        for (int i = 0; i < usuario.length(); i++) {
            char u = usuario.charAt(i);
            char c = correta.charAt(i);

            if (u == c)
                feedback.add("correto");
            else if (correta.indexOf(u) >= 0)
                feedback.add("perto");
            else
                feedback.add("errado");
        }

        boolean sucesso = usuario.equals(correta);

        return sucesso(feedback, sucesso);
    }

    private ValidacaoResultado validarNumero(String resposta, Desafio desafio) {

        try {
            int usuario = Integer.parseInt(resposta);
            int correta = Integer.parseInt(desafio.getDsResposta());

            if (usuario == correta) {
                return sucesso(null, true);
            }

            return new ValidacaoResultado(
                    true,
                    false,
                    usuario > correta ? "alto" : "baixo",
                    List.of(),
                    null);

        } catch (Exception e) {
            return erro("Resposta deve ser numérica");
        }
    }

    private ValidacaoResultado validarQuiz(String resposta, Desafio desafio) {

        boolean sucesso = resposta.equalsIgnoreCase(desafio.getDsResposta());

        return sucesso(null, sucesso);
    }

    private ValidacaoResultado validarPadrao(String resposta, Desafio desafio) {

        boolean sucesso = resposta.equalsIgnoreCase(desafio.getDsResposta());

        return sucesso(null, sucesso);
    }

    private ValidacaoResultado erro(String mensagem) {
        return new ValidacaoResultado(false, false, null, null, mensagem);
    }

    private ValidacaoResultado sucesso(List<String> feedback, boolean sucesso) {
        return new ValidacaoResultado(
                true,
                sucesso,
                sucesso ? "correto" : "errado",
                feedback,
                null);
    }
}