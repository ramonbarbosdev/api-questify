package com.api_questify.dto;

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
    private String respostaUsuario;
    private Object feedback;
}
