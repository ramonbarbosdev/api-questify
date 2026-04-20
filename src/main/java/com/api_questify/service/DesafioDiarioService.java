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

        try {

            String today = data.toString();

            String prompt = """
                    Gere um desafio inedito do tipo PALAVRA sobre o tema: %s.

                    IMPORTANTE:
                    A resposta DEVE obrigatoriamente ser uma palavra com EXATAMENTE 5 letras.
                    Se nao for possivel, gere outra ate atender essa regra.

                    Retorne no formato JSON exato:

                    {
                    "dsPergunta": "",
                    "dsResposta": "",
                    "tpDificuldade": "FACIL",
                    "tpDesafio": "PALAVRA",
                    "dtInicio": "%s",
                    "dtFim": "%s"
                    }

                    REGRAS:
                    - Retorne APENAS JSON valido
                    - Nao use markdown nem ```json
                    - Nao invente campos

                    - A resposta deve conter EXATAMENTE 5 letras (obrigatorio)
                    - A resposta deve conter apenas letras A-Z
                    - A resposta nao pode conter espacos
                    - A resposta nao pode conter acentos

                    - A pergunta deve levar a uma resposta de 5 letras

                    VALIDACAO:
                    - Conte as letras da resposta
                    - Se nao tiver 5 letras, descarte e gere novamente
                     """.formatted(tema, today, today);

            DesafioRequestDTO dto = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(DesafioRequestDTO.class);

            validarRespostaDaIa(dto, data);

            return dto;
        } catch (Exception e) {
            e.printStackTrace(); 
            throw new RuntimeException("Failed to generate content: " + e.getMessage(), e);
        }
    }

    private void validarRespostaDaIa(DesafioRequestDTO dto, LocalDate data) {
        if (dto == null) {
            throw new BusinessException("IA nao retornou desafio");
        }

        if (dto.getDsPergunta() == null || dto.getDsPergunta().trim().isEmpty()) {
            throw new BusinessException("IA retornou pergunta vazia");
        }

        if (dto.getDsResposta() == null || dto.getDsResposta().trim().isEmpty()) {
            throw new BusinessException("IA retornou resposta vazia");
        }

        if (dto.getTpDificuldade() == null) {
            dto.setTpDificuldade(Dificuldade.FACIL);
        }

        if (dto.getTpDesafio() == null) {
            dto.setTpDesafio(TipoDesafio.PALAVRA);
        }

        if (dto.getTpDesafio() != TipoDesafio.PALAVRA) {
            throw new BusinessException("IA retornou tipo de desafio diferente de PALAVRA");
        }

        dto.setDtInicio(data);
        dto.setDtFim(data);
    }
}
