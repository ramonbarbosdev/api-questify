package com.api_nivra.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_nivra.dto.DesafioDiarioResponseDTO;
import com.api_nivra.dto.DesafioRequestDTO;
import com.api_nivra.exception.BusinessException;
import com.api_nivra.exception.ResourceNotFoundException;
import com.api_nivra.model.Desafio;
import com.api_nivra.model.DesafioAgenda;
import com.api_nivra.repository.DesafioRepository;

@Service
public class DesafioService {

    @Autowired
    private DesafioRepository repository;

    @Autowired
    private DesafioAgendaService agendaService;


    @Transactional(rollbackFor = Exception.class)
    public void salvar(DesafioRequestDTO dto) {

        Desafio objeto = new Desafio();
        objeto.setDsPergunta(dto.getDsPergunta());
        objeto.setDsResposta(dto.getDsResposta());
        objeto.setTpDificuldade(dto.getTpDificuldade());
        objeto.setTpDesafio(dto.getTpDesafio());

        repository.save(objeto);

        agendaService.criarAgendaPorDesafio(dto, objeto);
    }

    public Desafio obterPorId(Long id) throws Exception {
        Optional<Desafio> objeto = repository.findById(id);

        if (!objeto.isPresent()) {
            throw new ResourceNotFoundException("Não existe desafio.");

        }

        return objeto.get();
    }

    public List<Desafio> obterDesafiosAtivos() {

        List<Desafio> objetos = agendaService.obterDesafiosAtivos();
        return objetos;
    }

    public Desafio obterDesafioAtivoPorId(Long idDesafio) {

        LocalDate agora = LocalDate.now();
        Boolean ativo = agendaService.existeDesafioNoPeriodo(idDesafio, agora);

        if (!ativo) {
            throw new BusinessException("Desafio não está ativo no momento");
        }

        return repository.findById(idDesafio)
                .orElseThrow(() -> new ResourceNotFoundException("Desafio não encontrado"));
    }
}
