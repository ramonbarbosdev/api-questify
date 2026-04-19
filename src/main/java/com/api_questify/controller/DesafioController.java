package com.api_questify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_questify.dto.ApiResponseDTO;
import com.api_questify.dto.DesafioComResultadoDTO;
import com.api_questify.dto.DesafioDiarioResponseDTO;
import com.api_questify.dto.DesafioQuizRequestDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.IdResponseDTO;
import com.api_questify.model.Desafio;
import com.api_questify.service.DesafioFacadeService;
import com.api_questify.service.DesafioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/desafio")
@Tag(name = "Desafios")
public class DesafioController {

    @Autowired
    private DesafioService service;

    @Autowired
    private DesafioFacadeService facade;

    @GetMapping("/ativos")
    public ResponseEntity<List<DesafioDiarioResponseDTO>> obterDesafiosAtivos() {
        return ResponseEntity.ok(service.obterDesafios());
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<IdResponseDTO>> criarDesafio(
            @RequestBody @Valid DesafioRequestDTO dto) {

        Desafio desafio = service.salvar(dto);

        return ResponseEntity
                .status(201)
                .body(new ApiResponseDTO<>(
                        "Desafio criado com sucesso",
                        new IdResponseDTO(desafio.getIdDesafio())));
    }

    @PostMapping("/quiz")
    public ResponseEntity<ApiResponseDTO<IdResponseDTO>> criarDesafioQuiz(
            @RequestBody @Valid DesafioQuizRequestDTO dto) {

        Desafio desafio = service.salvarQuiz(dto);

        return ResponseEntity
                .status(201)
                .body(new ApiResponseDTO<>(
                        "Quiz criado com sucesso",
                        new IdResponseDTO(desafio.getIdDesafio())));
    }

    @GetMapping("/atual/{idDispositivo}")
    public ResponseEntity<List<DesafioComResultadoDTO>> obterDesafioResultados(
            @PathVariable String idDispositivo) {

        return ResponseEntity.ok(
                facade.obterDesafiosComResultado(idDispositivo));
    }

    @DeleteMapping("/{idDesafio}")
    public ResponseEntity<Void> deletar(@PathVariable Long idDesafio) {

        service.deletar(idDesafio);

        return ResponseEntity.noContent().build();
    }
}
