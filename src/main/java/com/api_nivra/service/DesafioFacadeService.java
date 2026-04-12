package com.api_nivra.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_nivra.dto.DesafioComResultadoDTO;
import com.api_nivra.dto.ResultadoDTO;
import com.api_nivra.enums.TipoDesafio;
import com.api_nivra.model.Desafio;
import com.api_nivra.model.Resultado;
import com.api_nivra.repository.ResultadoRepository;

@Service
public class DesafioFacadeService {

    @Autowired
    private DesafioService desafioService;

    @Autowired
    private ResultadoRepository resultadoRepository;

     public List<DesafioComResultadoDTO> obterDesafiosComResultado(String idDispositivo) {

        List<Desafio> desafios = desafioService.obterDesafiosAtivos();

        return desafios.stream().map(desafio -> {

            DesafioComResultadoDTO dto = new DesafioComResultadoDTO();
            dto.setIdDesafio(desafio.getIdDesafio());
            dto.setDsPergunta(desafio.getDsPergunta());

            // =========================
            // 🔎 buscar tentativas
            // =========================
            List<Resultado> tentativas = resultadoRepository
                    .buscarPorDesafioEdispositivo(
                            desafio.getIdDesafio(),
                            idDispositivo
                    );

            // =========================
            // 🟡 nunca respondeu
            // =========================
            if (tentativas.isEmpty()) {
                dto.setRespondido(false);
                dto.setResultado(null);
                return dto;
            }

            // =========================
            // 🧠 respostas do usuário
            // =========================
            List<String> respostas = tentativas.stream()
                    .map(Resultado::getDsResposta)
                    .toList();

            // =========================
            // 🧠 status já persistido
            // =========================
            List<String> tentativasStatus = tentativas.stream()
                    .map(Resultado::getTpStatus)
                    .toList();

            // =========================
            // 🎯 sucesso
            // =========================
            boolean sucesso = tentativasStatus.contains("correct");

            // =========================
            // 📦 montar resultado
            // =========================
            ResultadoDTO resultadoDTO = new ResultadoDTO();
            resultadoDTO.setSucesso(sucesso);
            resultadoDTO.setRespostas(respostas);
            resultadoDTO.setTentativas(tentativasStatus);

            dto.setRespondido(true);
            dto.setResultado(resultadoDTO);

            return dto;

        }).toList();
    }

}
