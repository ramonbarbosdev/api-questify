package com.api_nivra.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoResultado {
  private boolean valido; // entrada válida?
  private boolean sucesso; // acertou?
  private String status; // correct | close | wrong
  private List<String> feedback; // opcional por tipo
  private String mensagem; // erro

}
