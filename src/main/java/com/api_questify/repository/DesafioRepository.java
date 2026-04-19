package com.api_questify.repository;

import org.springframework.stereotype.Repository;

import com.api_questify.model.Desafio;

import jakarta.transaction.Transactional;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
@Transactional
public interface DesafioRepository extends JpaRepository<Desafio, Long> {

}
