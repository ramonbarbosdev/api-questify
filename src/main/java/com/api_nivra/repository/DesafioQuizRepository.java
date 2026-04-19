package com.api_nivra.repository;

import org.springframework.stereotype.Repository;

import com.api_nivra.model.Desafio;
import com.api_nivra.model.DesafioQuiz;

import jakarta.transaction.Transactional;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface DesafioQuizRepository extends JpaRepository<DesafioQuiz, Long> {
    

}
