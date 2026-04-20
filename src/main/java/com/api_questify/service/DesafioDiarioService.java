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

        String today = data.toString();

        String prompt = """
                Gere um desafio inedito do tipo PALAVRA sobre o tema: %s.
                Retorne no formato JSON exato:

                {
                  "dsPergunta": "",
                  "dsResposta": "",
                  "tpDificuldade": "FACIL",
                  "tpDesafio": "PALAVRA",
                  "dtInicio": "%s",
                  "dtFim": "%s"
                }

               REGRAS OBRIGATORIAS:
                - Retorne APENAS JSON valido
                - Nao use markdown nem ```json
                - Nao invente campos

                - A resposta deve ser UMA unica palavra
                - A resposta deve ter EXATAMENTE 5 letras
                - A resposta deve conter apenas letras (A-Z)
                - A resposta nao pode ter acentos, espacos ou caracteres especiais

                - A pergunta deve ser formulada de forma que a resposta tenha 5 letras

               VALIDACAO FINAL (OBRIGATORIO):
                - Conte as letras da resposta
                - Se nao tiver exatamente 5 letras, DESCARTE e gere novamente
                - So retorne quando a resposta tiver 5 letras
                """.formatted(tema, today, today);

        DesafioRequestDTO dto = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(DesafioRequestDTO.class);

        validarRespostaDaIa(dto, data);

        return dto;
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
