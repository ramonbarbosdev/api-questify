package com.api_questify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.api_questify.enums.TipoDesafio;
import com.api_questify.exception.BusinessException;
import com.api_questify.interfaces.DesafioEstrategia;

@Service
public class DesafioEstrategiaFactory {

    private final Map<TipoDesafio, DesafioEstrategia> strategies = new HashMap<>();

    public DesafioEstrategiaFactory(List<DesafioEstrategia> strategyList) {
        for (DesafioEstrategia s : strategyList) {
            strategies.put(s.getTipo(), s);
        }
    }

    public DesafioEstrategia getEstrategia(TipoDesafio tipo) {
        DesafioEstrategia strategy = strategies.get(tipo);

        if (strategy == null) {
            throw new BusinessException("Tipo nao suportado: " + tipo);
        }

        return strategy;
    }
}
