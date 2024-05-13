package com.bsf.service;

import java.util.List;
import java.util.Optional;

import com.bsf.model.Orden;
import com.bsf.model.Usuario;

public interface IOrdenService {
	
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save(Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario(Usuario usuario);
}