package com.api_questify.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_questify.model.DesafioAgenda;

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

        List<DesafioAgenda> findAllByIdDesafio(Long idDesafio);
}