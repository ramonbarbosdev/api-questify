package com.api_questify.model;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
// @Table(name = "agenda_desafio", uniqueConstraints = {
//     @UniqueConstraint(name = "uk_agenda_desafio_dt_inicio", columnNames = "dt_inicio")
// })
public class DesafioAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_agendadesafio")
    @SequenceGenerator(name = "seq_agendadesafio", sequenceName = "seq_agendadesafio", allocationSize = 1)
    @Column(name = "id_agendadesafio")
    private Long idAgendaDesafio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_desafio", insertable = false, updatable = false)
    @JsonIgnore
    private Desafio desafio;

    @Column(name = "id_desafio", nullable = false)
    @NotNull(message = "O desafio obrigatorio!")
    private Long idDesafio;

    @NotNull(message = "A data de inicio é obrigatorio!")
    @Column(name = "dt_inicio")
    private LocalDate dtInicio;

    @NotNull(message = "A data do fim é obrigatorio!")
    @Column(name = "dt_fim")
    private LocalDate dtFim;

}
