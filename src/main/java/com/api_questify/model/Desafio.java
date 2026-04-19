package com.api_questify.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.api_questify.enums.Dificuldade;
import com.api_questify.enums.TipoDesafio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "desafio")
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_desafio")
    @SequenceGenerator(name = "seq_desafio", sequenceName = "seq_desafio", allocationSize = 1)
    @Column(name = "id_desafio")
    private Long idDesafio;

    @NotNull(message = "A pergunta é obrigatorio!")
    @Column(name = "ds_pergunta")
    private String dsPergunta;

    @NotNull(message = "A resposta é obrigatorio!")
    @Column(name = "ds_resposta")
    private String dsResposta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_desafio")
    private TipoDesafio tpDesafio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_dificuldade")
    private Dificuldade tpDificuldade;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @OneToOne(mappedBy = "desafio", cascade = CascadeType.ALL, orphanRemoval = true)
    private DesafioQuiz conteudo;

    public void setConteudo(DesafioQuiz quiz) {
        this.conteudo = quiz;

        if (quiz != null) {
            quiz.setDesafio(this); // 🔥 ESSENCIAL
        }
    }

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }

}
