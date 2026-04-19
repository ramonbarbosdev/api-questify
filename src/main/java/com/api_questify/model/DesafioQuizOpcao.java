package com.api_questify.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "desafio_quiz_opcao")
public class DesafioQuizOpcao {

    @Id
    @GeneratedValue
    @Column(name = "id_desafioquizopcap")
    private Long idDesafioQuizOpcap;

    @ManyToOne
    @JoinColumn(name = "id_desafio")
    private DesafioQuiz quiz;

    @Column(name = "cd_opcao")
    private String cdCodigo;

    @Column(name = "nm_rotulo")
    private String nmRotulo;
}