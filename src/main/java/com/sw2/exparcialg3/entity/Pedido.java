package com.sw2.exparcialg3.entity;

import com.sw2.exparcialg3.constantes.PedProdId;

import javax.persistence.*;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

    @Id
    @Column(name = "codigo")
    private String codigo;
    @Column(name = "fecha_compra",nullable = false)
    private LocalDate fecha_compra;
    @Column(name = "comprado", nullable = false)
    private int comprado;
    @Column(name = "total")
    private Float total;
    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuario;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id.pedido")
    private List<PedidoHasProducto> listPedidoHasProductos;

    public Pedido(){}
    public Pedido(String cod){
        this.setCodigo(cod);
    }
    @Transient
    private String creditCard;
    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public List<PedidoHasProducto> getListPedidoHasProductos() {
        return listPedidoHasProductos;
    }

    public void setListPedidoHasProductos(List<PedidoHasProducto> listPedidoHasProductos) {
        this.listPedidoHasProductos = listPedidoHasProductos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(LocalDate fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public int getComprado() {
        return comprado;
    }

    public void setComprado(int comprado) {
        this.comprado = comprado;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
