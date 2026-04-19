package com.api_nivra.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "desafio_quiz")
public class DesafioQuiz {

    @Id
    private Long idDesafio;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_desafio")
    private Desafio desafio;

    @Column(name = "fl_embaralhar")
    private Boolean flEmbaralhar;

    @Column(name = "nu_tempolimite")
    private Integer nuTempoLimite;

    @NotNull(message = "A resposta é obrigatória")
    @Column(name = "ds_respotacorreta")
    private String dsRespostaCorreta;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesafioQuizOpcao> opcoes;
}