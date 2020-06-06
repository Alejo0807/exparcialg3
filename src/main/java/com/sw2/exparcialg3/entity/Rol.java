package com.sw2.exparcialg3.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    @Id
    @Column(name = "idrol")
    private int idrol;
    @Column(name = "rol",nullable = false)
    private String rol;

    public Rol(){}

    public Rol(String rol){
        this.setRol(rol);
    }

    public Rol(int rol){
        this.setIdrol(rol);
    }

    public int getIdrol() {
        return idrol;
    }

    public void setIdrol(int idrol) {
        this.idrol = idrol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
