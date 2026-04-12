package com.api_nivra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespostaDTO {
     private boolean valido;
    private String mensagem;
    private String status;
    private Object feedback;
}

