package com.sw2.exparcialg3.constantes;

import com.sw2.exparcialg3.entity.Pedido;
import com.sw2.exparcialg3.entity.Producto;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class PedProdId implements Serializable {

    @ManyToOne
    @Column(name = "pedido")
    private Pedido pedido;
    @ManyToOne
    @Column(name = "producto")
    private Producto producto;

    public PedProdId(){}

    public PedProdId(Pedido p1, Producto p2){
        this.setPedido(p1);
        this.setProducto(p2);
    }


    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
