package com.api_questify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api_questify.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdDispositivo(String id);
}
