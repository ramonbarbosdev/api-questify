package com.api_questify.job;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.model.Desafio;
import com.api_questify.service.DesafioDiarioService;
import com.api_questify.service.DesafioService;

@Component
public class AgendadorJob {

    @Autowired
    private DesafioDiarioService service;

    @Autowired
    private DesafioService desafioService;

    @Scheduled(cron = "0 * * * * *", zone = "America/Sao_Paulo")
    // @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void gerarDiariamente() {

        System.out.println("Gerando desafio: " + LocalDateTime.now());

        DesafioRequestDTO dto = gerarDesafioUnico();

        desafioService.salvar(dto);
    }

    public DesafioRequestDTO gerarDesafioUnico() {

        int maxTentativas = 3;

        for (int i = 0; i < maxTentativas; i++) {

            DesafioRequestDTO dto = service.gerarDesafioDiario(); 

            boolean existe = desafioService.existePergunta(dto.getDsPergunta());

            if (!existe) {
                return dto;
            }

            System.out.println(" Desafio repetido, tentando novamente...");
        }

        throw new RuntimeException("Não foi possível gerar um desafio único");
    }
}
