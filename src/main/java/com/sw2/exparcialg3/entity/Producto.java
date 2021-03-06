package com.sw2.exparcialg3.entity;

import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name="producto")
public class Producto implements Serializable {

    @Id
    @NotBlank(message = "no puede estar vacío")
    @Column(name = "codigo")
    private String codigo;
    @Size(max = 40, message = "Debe tener 40 caracteres como máximo")
    @NotBlank(message = "no puede estar vacío")
    @Column(name = "nombre",nullable = false)
    private String nombre;
    @NotBlank
    @Size(max = 255, message = "Tiene que ser 255 caracteres como máximo")
    @Column(name = "descripcion",nullable = false)
    private String descripcion;
    @Column(name = "precio",nullable = false)
    private Float precio;
    @Column(name = "foto")
    private byte[] foto;
    @PositiveOrZero
    @Column(name = "stock",nullable = false)
    private int stock;
    private String fotocontenttype;

    public String getFotocontenttype() {
        return fotocontenttype;
    }

    public void setFotocontenttype(String fotocontenttype) {
        this.fotocontenttype = fotocontenttype;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
