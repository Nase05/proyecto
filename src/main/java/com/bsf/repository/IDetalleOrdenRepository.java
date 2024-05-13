package com.bsf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsf.model.DetalleOrden;

public interface IDetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {

}