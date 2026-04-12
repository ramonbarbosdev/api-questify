package com.api_nivra.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDTO {

    private boolean sucesso;
    private List<String> tentativas;
    private List<String> respostas;
    private List<List<String>> feedbacks;
}