package com.api_questify.service;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.model.ChatResult;
import com.api_questify.model.Desafio;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DesafioDiarioService {

    private final ChatClient chatClient;

    public DesafioDiarioService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public DesafioRequestDTO gerarDesafioDiario() {

        String today = LocalDate.now().toString();

        String prompt = """
                Gere um desafio do tipo PALAVRA no formato JSON:

                {
                  "dsPergunta": "",
                  "dsResposta": "",
                  "tpDificuldade": "FACIL",
                  "tpDesafio": "PALAVRA",
                  "dtInicio": "%s",
                  "dtFim": "%s"
                }

                REGRAS OBRIGATÓRIAS:
                - Retorne APENAS JSON válido (sem texto antes ou depois)
                - Não use ```json ou markdown
                - A resposta deve ser uma única palavra
                - Não invente campos
                - A resposta deve ter  5 palavras
                """.formatted(today, today);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(DesafioRequestDTO.class);
    }

    // public Desafio generate(TipoDesafio tipo, Dificuldade dificuldade) {

    // String prompt = buildPrompt(tipo, dificuldade);

    // String content = chatClient.prompt()
    // .user(prompt)
    // .call()
    // .content();

    // return parseAndValidate(content, tipo);
    // }

    // private String buildPrompt(TipoDesafio tipo, Dificuldade dificuldade) {

    // return switch (tipo) {

    // case PALAVRA -> """
    // Gere um desafio no formato JSON EXATO:

    // {
    // "dsPergunta": "...",
    // "dsResposta": "...",
    // "tpDificuldade": "%s",
    // "tpDesafio": "PALAVRA",
    // "dtInicio": "%s",
    // "dtFim": "%s"
    // }

    // Regras:
    // - Palavra simples
    // - Pode ser sinônimo, significado ou associação
    // - Resposta curta (1 palavra)
    // - Retorne apenas JSON válido
    // """.formatted(dificuldade, today(), today());

    // case NUMERO -> """
    // Gere um desafio matemático no formato JSON:

    // {
    // "dsPergunta": "...",
    // "dsResposta": "...",
    // "tpDificuldade": "%s",
    // "tpDesafio": "NUMERO",
    // "dtInicio": "%s",
    // "dtFim": "%s"
    // }

    // Regras:
    // - Pergunta com cálculo simples/médio/difícil
    // - Resposta deve ser numérica
    // - Retorne apenas JSON válido
    // """.formatted(dificuldade, today(), today());

    // case QUIZ -> """
    // Gere um desafio QUIZ no formato JSON EXATO:

    // {
    // "dsPergunta": "...",
    // "dsResposta": "...",
    // "tpDificuldade": "%s",
    // "tpDesafio": "QUIZ",
    // "dtInicio": "%s",
    // "dtFim": "%s",
    // "conteudo": {
    // "opcoes": [
    // { "cdOpcao": "A", "nmRotulo": "..." },
    // { "cdOpcao": "B", "nmRotulo": "..." },
    // { "cdOpcao": "C", "nmRotulo": "..." },
    // { "cdOpcao": "D", "nmRotulo": "..." }
    // ],
    // "dsRespostaCorreta": "A",
    // "flEmbaralhar": true,
    // "nuTempoLimite": 60
    // }
    // }

    // Regras:
    // - 4 opções obrigatórias
    // - Apenas 1 correta
    // - dsResposta deve bater com a alternativa correta
    // - Não repetir opções
    // - Retorne apenas JSON válido
    // """.formatted(dificuldade, today(), today());

    // case PADRAO -> """
    // Gere um desafio lógico (sequência/padrão) no formato JSON:

    // {
    // "dsPergunta": "...",
    // "dsResposta": "...",
    // "tpDificuldade": "%s",
    // "tpDesafio": "PADRAO",
    // "dtInicio": "%s",
    // "dtFim": "%s"
    // }

    // Regras:
    // - Ex: sequência numérica, padrão lógico, etc
    // - Resposta curta
    // - Retorne apenas JSON válido
    // """.formatted(dificuldade, today(), today());
    // };
    // }

    // private Desafio parseAndValidate(String json, TipoDesafio tipo) {

    // Desafio c = objectMapper.readValue(json, Desafio.class);

    // if (!c.getTpDesafio().equals(tipo.name())) {
    // throw new RuntimeException("Tipo incorreto");
    // }

    // if (c.getDsPergunta() == null || c.getDsResposta() == null) {
    // throw new RuntimeException("Campos obrigatórios ausentes");
    // }

    // if (tipo == TipoDesafio.QUIZ) {
    // validateQuiz(c);
    // }

    // return c;
    // }

    // private void validateQuiz(Desafio c) {

    // var conteudo = c.getConteudo();

    // if (conteudo.getOpcoes().size() != 4) {
    // throw new RuntimeException("Quiz deve ter 4 opções");
    // }

    // boolean hasCorrect = conteudo.getOpcoes().stream()
    // .anyMatch(o -> o.getCdOpcao()
    // .equals(conteudo.getDsRespostaCorreta()));

    // if (!hasCorrect) {
    // throw new RuntimeException("Resposta correta inválida");
    // }
    // }
}
