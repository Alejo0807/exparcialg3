package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {

    Optional<Pedido> findByComprado(int value);
}
