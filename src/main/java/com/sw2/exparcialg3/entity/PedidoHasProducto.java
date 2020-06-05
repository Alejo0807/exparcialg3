package com.sw2.exparcialg3.entity;

import com.sw2.exparcialg3.constantes.PedProdId;

import javax.persistence.*;

@Entity
@Table(name = "pedido_has_producto")
public class PedidoHasProducto {

    @EmbeddedId
    private PedProdId id;
    @Column(name = "cant", nullable = false)
    private int cant;
    @Column(name = "subtotal")
    private Float subtotal;

    public PedProdId getId() {
        return id;
    }

    public void setId(PedProdId id) {
        this.id = id;
    }

    public int getCant() {
        return cant;
    }

    public void setCant(int cant) {
        this.cant = cant;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }
}
