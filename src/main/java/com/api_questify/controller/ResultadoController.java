package com.api_questify.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_questify.dto.DesafioComResultadoDTO;
import com.api_questify.dto.DesafioRequestDTO;
import com.api_questify.dto.ResultadoRequestDTO;
import com.api_questify.dto.ResultadoResponseDTO;
import com.api_questify.model.Resultado;
import com.api_questify.service.ResultadoService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/resultado")
@Tag(name = "Resultado")
public class ResultadoController {

    @Autowired
    private ResultadoService service;

    @PostMapping("/resposta")
    public ResponseEntity<ResultadoResponseDTO> resposta(@RequestBody ResultadoRequestDTO dto) throws Exception {
        ResultadoResponseDTO resposta = service.responderDesafio(dto);

        return ResponseEntity.ok(resposta);

    }

}
