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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.pedido")
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

    public Pedido(Pedido oldPedido, int autoincremental){
        Pedido newPedido = new Pedido();
        LocalDate lt = LocalDate.now();
        newPedido.setCodigo("PE" + lt.getDayOfMonth() + lt.getMonthValue() + lt.getYear() + (autoincremental+1));
        newPedido.setFecha_compra(lt);
        newPedido.setComprado(1);
        newPedido.setUsuario(oldPedido.usuario);
        newPedido.setListPedidoHasProductos(new ArrayList<PedidoHasProducto>(){
            {
                oldPedido.getListPedidoHasProductos().forEach((php)->
                {
                    add(new PedidoHasProducto(new PedProdId(newPedido,php.getId().getProducto()), php.getCant()));
                    remove(php);
                });
            }
        });
        oldPedido.setTotal((float)0);
        oldPedido.setComprado(0);
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
