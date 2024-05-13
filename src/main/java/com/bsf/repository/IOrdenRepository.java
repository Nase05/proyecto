package com.bsf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsf.model.Orden;
import com.bsf.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
	
	List<Orden> findByUsuario(Usuario usuario);

}