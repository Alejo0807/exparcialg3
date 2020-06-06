package com.sw2.exparcialg3.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "dni")
    private int dni;
    @NotBlank
    @Size(min = 2, max = 40)
    @Column(name = "nombre",nullable = false)
    private String nombre;
    @NotBlank
    @Size(min = 2, max = 40)
    @Column(name = "apellido",nullable = false)
    private String apellido;
    @NotBlank
    @Column(name = "correo",nullable = false)
    private String correo;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "enable",nullable = false)
    private boolean enable;
    @ManyToOne
    @JoinColumn(name = "rol")
    private Rol rol;

    public String getFullname(){
        return nombre + " " + apellido;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
