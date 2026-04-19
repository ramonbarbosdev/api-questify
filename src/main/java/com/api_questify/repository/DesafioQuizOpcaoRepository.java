package com.api_questify.repository;

import org.springframework.stereotype.Repository;

import com.api_questify.model.DesafioQuizOpcao;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface DesafioQuizOpcaoRepository extends JpaRepository<DesafioQuizOpcao, Long> {

}
