package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Producto;
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
    Integer hallarAutoincrementalPedido();

    @Procedure(procedureName = "udpate_carrito")
    void udpate_carrito(String code, float total);

    @Procedure(procedureName = "new_pedido")
    void new_pedido(String codigo, int usuario, float total);

    List<Pedido> findByUsuarioAndComprado(Usuario usuario, int comprado);

    @Query(value = "select * from pedido where (codigo like concat(\"%\",?1,\"%\")) and (usuario like ?2) and (comprado like 1);" , nativeQuery = true)
    List<Pedido> buscarPedidos(String param, int dni);

}
