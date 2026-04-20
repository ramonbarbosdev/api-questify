package com.api_questify.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.DesafioRequestGeralDTO;
import com.api_questify.exception.BusinessException;
import com.api_questify.model.Desafio;
import com.api_questify.model.DesafioAgenda;
import com.api_questify.repository.DesafioAgendaRepository;

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

        public Boolean existeDesafioNoPeriodo(Long idDesafio, LocalDate agora) {
                return repository
                                .existsByDesafioIdDesafioAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                                                idDesafio,
                                                agora,
                                                agora);
        }

        public boolean existeDesafioNaData(LocalDate data) {
                return repository.existsByDtInicioLessThanEqualAndDtFimGreaterThanEqual(data, data);
        }

        public void criarAgendaPorDesafio(DesafioRequestGeralDTO dto, Desafio desafio) {

                if (dto.getDtInicio().isAfter(dto.getDtFim())) {
                        throw new BusinessException("Data início não pode ser maior que data fim");
                }

                // boolean conflito = repository
                //                 .existsByDtInicioLessThanEqualAndDtFimGreaterThanEqual(
                //                                 dto.getDtFim(),
                //                                 dto.getDtInicio());

                // if (conflito) {
                //         throw new BusinessException("Ja existe um desafio ativo nesse periodo");
                // }

                DesafioAgenda agenda = new DesafioAgenda();
                agenda.setIdDesafio(desafio.getIdDesafio());
                agenda.setDtInicio(dto.getDtInicio());
                agenda.setDtFim(dto.getDtFim());

                repository.save(agenda);
        }

        public void deletarRegistros(Long idDesafio) {
                List<DesafioAgenda> objetos = repository.findAllByIdDesafio(idDesafio);

                if (objetos == null) {
                        return;
                }

                for (DesafioAgenda objeto : objetos) {
                        repository.delete(objeto);
                }
        }
}
