package com.bsf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsf.model.Producto;
import com.bsf.repository.IProductoRepository;

@Service
public class ProductoServiceImpl implements IProductoService{

	@Autowired
	private IProductoRepository productoRepository;
	
	@Override
	public Producto save(Producto producto) {
		return productoRepository.save(producto);
	}

	@Override
	public Optional<Producto> get(Integer id) {
		return productoRepository.findById(id);
	}

	@Override
	public void update(Producto producto) {
		productoRepository.save(producto);
		
	}

	@Override
	public void delete(Integer id) {
		productoRepository.deleteById(id);
		
	}

	@Override
	public List<Producto> findAll() {
		return productoRepository.findAll();
	}

	public Optional<Producto> findById(Integer id) {
		return productoRepository.findById(id);
	}

}