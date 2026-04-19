package com.api_questify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api_questify.model.Resultado;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    Optional<Resultado> findTopByIdDesafioOrderByIdResultadoDesc(Long id);

    @Query("""
                SELECT r FROM Resultado r
                WHERE r.idDesafio = :idDesafio
                AND r.usuario.idDispositivo = :idDispositivo
                ORDER BY r.nuTentativa
            """)
    List<Resultado> buscarPorDesafioEdispositivo(
            Long idDesafio,
            String idDispositivo);
}
