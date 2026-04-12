package com.api_nivra.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "resultado")
public class Resultado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_resultado")
    @SequenceGenerator(name = "seq_resultado", sequenceName = "seq_resultado", allocationSize = 1)
    @Column(name = "id_resultado")
    private Long idResultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "id_usuario", nullable = false)
    @NotNull(message = "O Usuario obrigatorio!")
    private Long idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_desafio", insertable = false, updatable = false)
    @JsonIgnore
    private Desafio desafio;

    @Column(name = "id_desafio", nullable = false)
    @NotNull(message = "O desafio obrigatorio!")
    private Long idDesafio;

    @Column(name = "ds_resposta")
    private String dsResposta;

    @Column(name = "nu_tentativa")
    private Integer nuTentativa;

    @Column(name = "fl_sucesso")
    private Boolean flSucesso;

    @Column(name = "tp_status")
    private String tpStatus;

    @Column(name = "ds_feedback")
    private String dsFeedback;

    @Column(name = "dt_concluido")
    private LocalDateTime dtConcluido;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @PrePersist
    protected void onCreate() {
        this.dtCadastro = LocalDateTime.now();
    }
}
