package com.bsf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bsf.model.Comentarios;
import com.bsf.model.Producto;
import java.util.List;

public interface IComentarioRepository extends JpaRepository<Comentarios, Integer> {
    List<Comentarios> findByProducto(Producto producto);
}
