package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {

    Optional<Pedido> findByComprado(int value);

    @Procedure(procedureName = "hallar_autoincremental_pedido")
    int hallarAutoincrementalPedido();

    List<Pedido> findByUsuarioAndComprado(Usuario usuario, int comprado);

}
