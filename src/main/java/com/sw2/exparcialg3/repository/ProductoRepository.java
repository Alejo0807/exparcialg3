package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {


    List<Producto> findByStockIsGreaterThan(int stock);

    @Procedure(name = "update_producto")
    void update_producto(String codigo, String nombre, String descripcion, int stock);

}
