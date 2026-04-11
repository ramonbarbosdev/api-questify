package com.api_nivra.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_nivra.dto.DesafioRequestDTO;
import com.api_nivra.exception.BusinessException;
import com.api_nivra.model.Desafio;
import com.api_nivra.model.DesafioAgenda;
import com.api_nivra.repository.DesafioAgendaRepository;

@Service
public class DesafioAgendaService {

    @Autowired
    private DesafioAgendaRepository repository;

    public List<Desafio> obterDesafiosAtivos() {

        LocalDate agora = LocalDate.now();

        List<DesafioAgenda> agendas = repository
                .findByDtInicioLessThanEqualAndDtFimGreaterThanEqual(agora, agora);

        return agendas.stream()
                .map(DesafioAgenda::getDesafio)
                .toList();
    }

    public Boolean existeDesafioNoPeriodo(Long idDesafio, LocalDate agora)
    {
            return  repository
            .existsByDesafioIdDesafioAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                    idDesafio,
                    agora,
                    agora
            );
    }

    public void criarAgendaPorDesafio(DesafioRequestDTO dto, Desafio desafio) {

        if (dto.getDtInicio().isAfter(dto.getDtFim())) {
            throw new BusinessException("Data início não pode ser maior que data fim");
        }

        boolean conflito = repository
                .existsByDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                        dto.getDtFim(),
                        dto.getDtInicio());

        // if (conflito) {
        //     throw new BusinessException("Já existe um desafio ativo nesse período");
        // }

        DesafioAgenda agenda = new DesafioAgenda();
        agenda.setIdDesafio(desafio.getIdDesafio());
        agenda.setDtInicio(dto.getDtInicio());
        agenda.setDtFim(dto.getDtFim());

        repository.save(agenda);
    }
}
