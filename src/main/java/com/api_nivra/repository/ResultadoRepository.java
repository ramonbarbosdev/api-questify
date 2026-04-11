package com.api_nivra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_nivra.model.Resultado;

public interface ResultadoRepository  extends JpaRepository<Resultado, Long>{

    Optional<Resultado> findTopByIdDesafioOrderByIdResultadoDesc(Long id);
} 
