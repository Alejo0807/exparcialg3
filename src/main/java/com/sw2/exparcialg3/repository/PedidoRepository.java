package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {

    Optional<Pedido> findByComprado(int value);

    @Query(value = "set @numero = 0;\n" +
            "call exparcial.hallarAutoincrementalPedido(@numero);\n" +
            "select @numero;", nativeQuery = true)
    Optional<Integer> hallarAutoincrementalPedido();

    List<Pedido> findByUsuario(Usuario usuario);

}
