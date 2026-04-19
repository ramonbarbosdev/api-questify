package com.api_nivra.repository;

import org.springframework.stereotype.Repository;

import com.api_nivra.model.DesafioQuizOpcao;

import jakarta.transaction.Transactional;


import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface DesafioQuizOpcaoRepository extends JpaRepository<DesafioQuizOpcao, Long> {
    

}
