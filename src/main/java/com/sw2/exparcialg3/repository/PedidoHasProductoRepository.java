package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.constantes.PedProdId;
import com.sw2.exparcialg3.entity.PedidoHasProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoHasProductoRepository extends JpaRepository<PedidoHasProducto, PedProdId> {
}
