package com.api_nivra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_nivra.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdDispositivo(String id);
} 
