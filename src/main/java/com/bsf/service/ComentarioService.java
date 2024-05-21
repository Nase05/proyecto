package com.bsf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsf.model.Comentarios;
import com.bsf.model.Producto;
import com.bsf.repository.IComentarioRepository;

@Service
public class ComentarioService {

    @Autowired
    private IComentarioRepository comentarioRepository;

    public List<Comentarios> obtenerComentariosPorProducto(Producto producto) {
        return comentarioRepository.findByProducto(producto);
    }

    public Comentarios guardarComentario(Comentarios comentario) {
        return comentarioRepository.save(comentario);
    }

	public List<Comentarios> findAll() {
		return comentarioRepository.findAll();
	}
}
