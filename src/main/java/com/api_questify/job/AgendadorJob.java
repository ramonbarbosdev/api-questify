package com.api_questify.job;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.service.DesafioAgendaService;
import com.api_questify.service.DesafioDiarioService;
import com.api_questify.service.DesafioService;

@Component
public class AgendadorJob {

    private static final Logger log = LoggerFactory.getLogger(AgendadorJob.class);
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final int MAX_TENTATIVAS = 3;
    private static final List<String> TEMAS = List.of(
            "astronomia",
            "biologia",
            "geografia",
            "historia",
            "tecnologia",
            "matematica recreativa",
            "cultura brasileira",
            "lingua portuguesa",
            "ciencia do cotidiano",
            "logica");

    private final DesafioDiarioService service;
    private final DesafioService desafioService;
    private final DesafioAgendaService agendaService;

    public AgendadorJob(
            DesafioDiarioService service,
            DesafioService desafioService,
            DesafioAgendaService agendaService) {
        this.service = service;
        this.desafioService = desafioService;
        this.agendaService = agendaService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void gerarDiariamente() {

        LocalDate hoje = LocalDate.now(ZONE_ID);
        log.info("Iniciando job de desafio diario. data={}", hoje);

        if (agendaService.existeDesafioNaData(hoje)) {
            log.info("Desafio diario ja existe. IA nao sera chamada. data={}", hoje);
            return;
        }

        DesafioRequestDTO dto = gerarDesafioUnico(hoje);
        desafioService.salvar(dto);

        log.info("Desafio diario criado com sucesso. data={} pergunta={}", hoje, dto.getDsPergunta());
    }

    public DesafioRequestDTO gerarDesafioUnico(LocalDate data) {

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {

            String tema = escolherTema(data, tentativa);
            DesafioRequestDTO dto = service.gerarDesafioDiario(tema, data);

            if (!desafioService.existePergunta(dto.getDsPergunta())) {
                return dto;
            }

            log.warn("IA gerou pergunta repetida. tentativa={} tema={} pergunta={}",
                    tentativa,
                    tema,
                    dto.getDsPergunta());
        }

        throw new IllegalStateException("Nao foi possivel gerar um desafio unico");
    }

    private String escolherTema(LocalDate data, int tentativa) {
        int index = Math.floorMod(data.hashCode() + tentativa - 1, TEMAS.size());
        return TEMAS.get(index);
    }
}
