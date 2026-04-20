package com.api_questify.service;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.interfaces.DesafioEstrategia;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class DesafioDiarioService {

    private final ChatClient chatClient;
    private final DesafioEstrategiaFactory factory;

    public DesafioDiarioService(ChatClient.Builder builder,
            DesafioEstrategiaFactory factory) {
        this.chatClient = builder.build();
        this.factory = factory;
    }

    public DesafioRequestGeralDTO gerarDesafioDiario(String tema, LocalDate data) {

        TipoDesafio tipo = sortearTipoDesafio();

        Dificuldade dificuldade = sortearDificuldade();

        DesafioEstrategia estrategia = factory.getEstrategia(tipo);

        String prompt = estrategia.gerarPrompt(tema, data, dificuldade);
        
        DesafioRequestGeralDTO dto = chatClient.prompt()
                .user(prompt)
                .call()
                .entity(DesafioRequestGeralDTO.class);

        estrategia.validar(dto, data);

        dto.setDtInicio(data);
        dto.setDtFim(data);

        return dto;
    }

    private Dificuldade sortearDificuldade() {
        Dificuldade[] values = Dificuldade.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    private TipoDesafio sortearTipoDesafio() {
        TipoDesafio[] values = TipoDesafio.values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}
