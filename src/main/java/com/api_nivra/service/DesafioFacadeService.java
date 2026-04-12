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

    public static final Integer MAXIMO_TENTATIVA = 5;

    public List<DesafioComResultadoDTO> obterDesafiosComResultado(String idDispositivo) {

        List<Desafio> desafios = desafioService.obterDesafiosAtivos();

        return desafios.stream().map(desafio -> {

            DesafioComResultadoDTO dto = new DesafioComResultadoDTO();
            dto.setIdDesafio(desafio.getIdDesafio());
            dto.setDsPergunta(desafio.getDsPergunta());
            dto.setTpDesafio(desafio.getTpDesafio());

            List<Resultado> tentativas = resultadoRepository
                    .buscarPorDesafioEdispositivo(
                            desafio.getIdDesafio(),
                            idDispositivo);

            if (tentativas.isEmpty()) {
                dto.setResultado(null);
                return dto;
            }

            List<String> respostas = tentativas.stream()
                    .map(Resultado::getDsResposta)
                    .toList();

            List<String> tentativasStatus = tentativas.stream()
                    .map(Resultado::getTpStatus)
                    .toList();

            // 🔥 NOVO
            List<List<String>> feedbacks = tentativas.stream()
                    .map(r -> desserializarFeedback(r.getDsFeedback()))
                    .toList();

            boolean sucesso = tentativas.stream()
                    .anyMatch(r -> Boolean.TRUE.equals(r.getFlSucesso()));

            boolean finalizar = tentativas.size() >= MAXIMO_TENTATIVA || sucesso;

            ResultadoDTO resultadoDTO = new ResultadoDTO();
            resultadoDTO.setSucesso(sucesso);
            resultadoDTO.setRespostas(respostas);
            resultadoDTO.setTentativas(tentativasStatus);
            resultadoDTO.setFeedbacks(feedbacks);

            dto.setFlFinalizado(finalizar);
            dto.setResultado(resultadoDTO);

            return dto;

        }).toList();
    }

    private List<String> desserializarFeedback(String feedback) {
        if (feedback == null || feedback.isBlank())
            return List.of();
        return List.of(feedback.split(","));
    }

}
