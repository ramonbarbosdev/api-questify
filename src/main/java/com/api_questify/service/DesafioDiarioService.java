package com.api_questify.service;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;

@Service
public class DesafioDiarioService {

    private final ChatClient chatClient;

    public DesafioDiarioService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public DesafioRequestDTO gerarDesafioDiario() {
        return gerarDesafioDiario("conhecimentos gerais", LocalDate.now());
    }

    public DesafioRequestDTO gerarDesafioDiario(String tema, LocalDate data) {
        return gerarComRetry(tema, data);
    }

    private DesafioRequestDTO gerarComRetry(String tema, LocalDate data) {

        for (int i = 0; i < 5; i++) {
            try {
                return gerarInterno(tema, data);
            } catch (Exception e) {
                System.out.println("Tentativa inválida: " + e.getMessage());
            }
        }

        throw new RuntimeException("Falha ao gerar desafio válido após várias tentativas");
    }

    private DesafioRequestDTO gerarInterno(String tema, LocalDate data) {

        String today = data.toString();

   
        String palavra = chatClient.prompt()
                .user("""
                        Gere uma palavra comum da lingua portuguesa com exatamente 5 letras.

                        Regras:
                        - Apenas uma palavra
                        - Sem acentos
                        - Sem espacos
                        - Apenas letras A-Z
                        - Palavra simples do dia a dia

                        Retorne somente a palavra.
                        """)
                .call()
                .content()
                .trim()
                .toUpperCase();

        validarPalavra(palavra);

        String promptPergunta = """
                Crie uma pergunta cuja resposta seja exatamente a palavra: "%s".

                Tema: %s

                Regras:
                - Não pode conter a palavra na pergunta
                - Deve ser clara e objetiva
                - Nível fácil
                - Baseada em conhecimento geral

                Retorne no formato JSON:

                {
                  "dsPergunta": "",
                  "dsResposta": "%s",
                  "tpDificuldade": "FACIL",
                  "tpDesafio": "PALAVRA",
                  "dtInicio": "%s",
                  "dtFim": "%s"
                }

                - Retorne apenas JSON válido
                - Não use markdown
                """.formatted(palavra, tema, palavra, today, today);

        DesafioRequestDTO dto = chatClient.prompt()
                .user(promptPergunta)
                .call()
                .entity(DesafioRequestDTO.class);

        validarResposta(dto, palavra, data);

        return dto;
    }

    private void validarPalavra(String palavra) {
        if (palavra == null || !palavra.matches("^[A-Z]{5}$")) {
            throw new BusinessException("Palavra inválida gerada pela IA: " + palavra);
        }
    }

    private void validarResposta(DesafioRequestDTO dto, String palavraEsperada, LocalDate data) {

        if (dto == null) {
            throw new BusinessException("IA não retornou desafio");
        }

        if (dto.getDsPergunta() == null || dto.getDsPergunta().isBlank()) {
            throw new BusinessException("Pergunta inválida");
        }

        if (!palavraEsperada.equalsIgnoreCase(dto.getDsResposta())) {
            throw new BusinessException("Resposta não corresponde à palavra gerada");
        }

        if (dto.getTpDificuldade() == null) {
            dto.setTpDificuldade(Dificuldade.FACIL);
        }

        if (dto.getTpDesafio() == null) {
            dto.setTpDesafio(TipoDesafio.PALAVRA);
        }

        if (dto.getTpDesafio() != TipoDesafio.PALAVRA) {
            throw new BusinessException("Tipo inválido retornado pela IA");
        }

        dto.setDtInicio(data);
        dto.setDtFim(data);
    }
}