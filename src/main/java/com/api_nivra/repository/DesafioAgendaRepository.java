package com.api_nivra.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_nivra.model.DesafioAgenda;

public interface DesafioAgendaRepository extends JpaRepository<DesafioAgenda, Long> {

    List<DesafioAgenda> findByDtInicioLessThanEqualAndDtFimGreaterThanEqual(
            LocalDate agora1,
            LocalDate agora2);

    boolean existsByDtInicioLessThanEqualAndDtFimGreaterThanEqual(
            LocalDate fim,
            LocalDate inicio);

    boolean existsByDesafioIdDesafioAndDtInicioLessThanEqualAndDtFimGreaterThanEqual(
            Long idDesafio,
            LocalDate inicio,
            LocalDate fim);

}