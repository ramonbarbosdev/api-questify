package com.api_nivra.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_nivra.model.Usuario;
import com.api_nivra.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario obterUsuarioPorDispositivo(String id) {
        Optional<Usuario> objeto = repository.findByIdDispositivo(id);

        if (!objeto.isPresent()) {
            return null;
        }

        return objeto.get();
    }

    public Usuario salvarDispositivo(String id) {

        Usuario objeto = new Usuario();
        objeto.setIdDispositivo(id);
        return repository.save(objeto);
    }
}
