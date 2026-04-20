package com.api_questify.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_questify.dto.DesafioBaseDTO;
import com.api_questify.dto.DesafioComResultadoDTO;
import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.QuizOpcaoResponseDTO;
import com.api_questify.dto.QuizResponseDTO;
import com.api_questify.dto.ResultadoDTO;
import com.api_questify.enums.TipoDesafio;
import com.api_questify.model.Desafio;
import com.api_questify.model.DesafioQuiz;
import com.api_questify.model.Resultado;
import com.api_questify.repository.ResultadoRepository;

@Service
public class DesafioFacadeService {

    public DesafioRequestDTO toPadrao(DesafioBaseDTO dto) {

        DesafioRequestDTO padrao = new DesafioRequestDTO();

        padrao.setDsPergunta(dto.getDsPergunta());
        padrao.setDsResposta(dto.getDsResposta());
        padrao.setDtInicio(dto.getDtInicio());
        padrao.setDtFim(dto.getDtFim());
        padrao.setTpDesafio(dto.getTpDesafio());
        padrao.setTpDificuldade(dto.getTpDificuldade());

        return padrao;
    }

    public Desafio montarEntidade(DesafioBaseDTO dto, String hashPergunta) {

        Desafio objeto = new Desafio();

        objeto.setDsPergunta(dto.getDsPergunta());
        objeto.setDsHashPergunta(hashPergunta);
        objeto.setTpDificuldade(dto.getTpDificuldade());
        objeto.setTpDesafio(dto.getTpDesafio());
        objeto.setDsResposta(dto.getDsResposta());
        objeto.setNuTamanhoResposta(dto.getDsResposta().length());

        return objeto;
    }

    // Auxiliares

}
