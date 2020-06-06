package com.sw2.exparcialg3.entity;

import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @Column(name = "dni")
    //@NotBlank(message = "Este campo no puede estar vacío")
    @Digits(integer = 8, fraction = 0, message = "El DNI tiene que tener 8 dígitos")
    private int dni;
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(min = 2, max = 40)
    @Column(name = "nombre",nullable = false)
    private String nombre;
    @NotBlank(message = "Este campo no puede estar vacío")
    @Size(min = 2, max = 40)
    @Column(name = "apellido",nullable = false)
    private String apellido;
    @NotBlank(message = "Este campo no puede estar vacío")
    @Column(name = "correo",nullable = false)
    private String correo;
    private String password;
    @Column(name = "enable",nullable = false)
    private boolean enable;
    @ManyToOne
    @JoinColumn(name = "rol")
    private Rol rol;


    public boolean validatePassword(){
        if(password!=null){
            int len = password.length();
            if (len>7 && len<11){
                String numbers = "0123456789";
                int count=0;
                for(String i : password.split("")){
                    if (numbers.contains(i)) count++;
                }
                if (count>=2){
                    this.password = new BCryptPasswordEncoder().encode(password);
                    return true;
                }

            }
        }
        return false;
    }

    public String generateNewPassword(){
        RandomString rs = new RandomString(8);
        int[] randomNum = {ThreadLocalRandom.current().nextInt(0, 9),ThreadLocalRandom.current().nextInt(0, 9)};
        String newpassword = rs+String.valueOf(randomNum[0])+String.valueOf(randomNum[1]);
        password = new BCryptPasswordEncoder().
                encode(newpassword);
        return newpassword;
    }

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
