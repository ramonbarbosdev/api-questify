package com.api_questify.job;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.service.DesafioAgendaService;
import com.api_questify.service.DesafioDiarioService;
import com.api_questify.service.DesafioService;

@Component
public class AgendadorJob {

    private static final Logger log = LoggerFactory.getLogger(AgendadorJob.class);
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final int MAX_TENTATIVAS = 3;
    private static final List<String> TEMAS = List.of(
            "Mitologia Mundial", "Culinária Internacional", "Grandes Invenções",
            "Exploração Espacial", "Fauna e Flora Africana", "Império Romano",
            "Pintores Famosos", "Cinema Clássico", "Esportes Olímpicos",
            "Capitais do Mundo", "Corpo Humano", "Literatura Universal",
            "Música e Instrumentos", "Oceanografia", "Eventos do Século XX");

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

        // if (agendaService.existeDesafioNaData(hoje)) {
        // log.info("Desafio diario ja existe. IA nao sera chamada. data={}", hoje);
        // return;
        // }

        DesafioRequestGeralDTO dto = gerarDesafioUnico(hoje);
        desafioService.salvar(dto);

        log.info("Desafio diario criado com sucesso. data={} pergunta={}", hoje, dto.getDsPergunta());
    }

    public DesafioRequestGeralDTO gerarDesafioUnico(LocalDate data) {

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {

            String tema = escolherTema(data, tentativa);
            DesafioRequestGeralDTO dto = service.gerarDesafioDiario(tema, data);

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
        int seed = data.getDayOfYear() + tentativa;
        return TEMAS.get(seed % TEMAS.size());
    }
}
