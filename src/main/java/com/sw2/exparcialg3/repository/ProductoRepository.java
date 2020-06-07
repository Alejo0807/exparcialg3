package com.sw2.exparcialg3.repository;

import com.sw2.exparcialg3.dto.*;
import com.sw2.exparcialg3.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {


    List<Producto> findByStockIsGreaterThan(int stock);

    @Procedure(name = "update_producto")
    void update_producto(String codigo, String nombre, String descripcion, int stock);


    @Query(value = "select * from producto where nombre like concat(\"%\",?1,\"%\") or codigo like concat(\"%\",?1,\"%\") and stock>0 limit ?2,?3" , nativeQuery = true)
    List<Producto> buscarProductos(String param,int limit1, int limit2);


    @Query(value = "select count(*) as cantidad from producto where nombre like concat(\"%\",?1,\"%\") or codigo like concat(\"%\",?1,\"%\") and stock >0", nativeQuery = true)
    CountProductsDto contar(String param);

    //Para las estadísticas:

    @Query(value = "select count(*) as cantidad, producto  from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo)\n" +
            " where comprado =1 group by producto order by cantidad desc limit 1", nativeQuery = true)
    ProductoMasVendidoDto getProdcutoMasVendidoDto();

    @Query(value = "select count(*) as cantidad, producto  from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo)\n" +
            "  group by producto order by cantidad asc limit 1", nativeQuery = true)
    ProductoMasVendidoDto getProdcutoMenosVendidoDto();

    @Query(value = "select count(*) as cantidad, producto  from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo)\n" +
            " where comprado =1 group by producto order by cantidad asc limit 1", nativeQuery = true)
    ProductoMasCaro getProductoMasCaro();

    @Query(value = "select count(*) as cantidad  from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo)\n" +
            " where comprado =1", nativeQuery = true)
    CantidadProductosVendidos getCantidadProductosVendidos();

    @Query(value = "select count(*) as cantidad from pedido where comprado =1", nativeQuery = true)
    GetCantidadCompras getGetCantidadCompras();

    @Query(value = "select sum(total) as total from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo) where comprado =1",
    nativeQuery = true)
    TotalFacturado getTotalFacturado();

    @Query(value = "select sum(total) as facturado, usuario from pedido p inner join pedido_has_producto php on (php.pedido=p.codigo) where comprado = 1\n" +
            "group by usuario order by facturado desc limit 1", nativeQuery = true)
    UsuarioQueMasGasto getUsuarioQueMasGasto();


}
