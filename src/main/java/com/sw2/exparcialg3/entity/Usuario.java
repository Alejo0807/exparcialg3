package com.sw2.exparcialg3.entity;

import javax.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "dni")
    private int dni;
    @Column(name = "nombre",nullable = false)
    private int nombre;
    @Column(name = "apellido",nullable = false)
    private int apellido;
    @Column(name = "correo",nullable = false)
    private int correo;
    @Column(name = "contraseña",nullable = false)
    private int contraseña;
    @Column(name = "enable",nullable = false)
    private int enable;
    @ManyToOne
    @JoinColumn(name = "rol")
    private Rol rol;

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public int getApellido() {
        return apellido;
    }

    public void setApellido(int apellido) {
        this.apellido = apellido;
    }

    public int getCorreo() {
        return correo;
    }

    public void setCorreo(int correo) {
        this.correo = correo;
    }

    public int getContraseña() {
        return contraseña;
    }

    public void setContraseña(int contraseña) {
        this.contraseña = contraseña;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
