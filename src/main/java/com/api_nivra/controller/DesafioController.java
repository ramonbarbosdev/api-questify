package com.api_nivra.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_nivra.dto.DesafioComResultadoDTO;
import com.api_nivra.dto.DesafioDiarioResponseDTO;
import com.api_nivra.dto.DesafioQuizRequestDTO;
import com.api_nivra.dto.DesafioRequestDTO;
import com.api_nivra.model.Desafio;
import com.api_nivra.service.DesafioFacadeService;
import com.api_nivra.service.DesafioService;
import com.api_nivra.service.ResultadoService;

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
    public List<DesafioDiarioResponseDTO> obterDesafiosAtivos() {

        return service.obterDesafios();
    }

    @PostMapping("/")
    public ResponseEntity<?> criarDesafio(@RequestBody @Valid DesafioRequestDTO dto) {
        Desafio objeto = service.salvar(dto);
        return ResponseEntity.created(null).body(Map.of("resposta", "Registro salvo", "id", objeto.getIdDesafio()));
    }

    @PostMapping("/quiz")
    public ResponseEntity<?> criarDesafioQuiz(@RequestBody DesafioQuizRequestDTO dto) {

        Desafio objeto = service.salvarQuiz(dto);
        return ResponseEntity.created(null).body(Map.of("resposta", "Registro salvo", "id", objeto.getIdDesafio()));

    }

    @GetMapping("/atual/{idDispositivo}")
    public ResponseEntity<List<DesafioComResultadoDTO>> obterDesafioResultados(@PathVariable String idDispositivo) {
        List<DesafioComResultadoDTO> objeto = facade.obterDesafiosComResultado(idDispositivo);

        return ResponseEntity.ok(objeto);

    }

    @DeleteMapping("/{idDesafio}")
    public ResponseEntity<?> deletar(@PathVariable Long idDesafio) {

        service.deletar(idDesafio);

       return ResponseEntity.noContent().build();
    }
}
