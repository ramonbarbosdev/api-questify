package com.api_nivra.dto;

import java.time.LocalDate;

import com.api_nivra.enums.Dificuldade;
import com.api_nivra.enums.TipoDesafio;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesafioDiarioResponseDTO
{
    private Long idDesafio;
    private String dsPergunta;
    private String dsResposta;
    private Dificuldade tpDificuldade;
    private TipoDesafio tpDesafio;
}
