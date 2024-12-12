package org.egibide.Modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Usuario implements Serializable {
    private String nombre;
    private String apellido;
    //private LocalDate fechaNacimiento;
    private int edad;
    private String email;
    private String usuario;
    private byte[] pass;
    private String passTexto;


    public Usuario() {
    }

    public Usuario(String nombre, String apellido, String email, String usuario, byte[] pass) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.usuario = usuario;
        this.pass = pass;
    }

    public Usuario(String nombre, String apellido, int edad, String email, String usuario, String passTexto) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.passTexto = passTexto;
    }

    // region Getters y setters

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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public byte[] getPass() {
        return pass;
    }

    public void setPass(byte[] pass) {
        this.pass = pass;
    }

    public String getPassTexto() {
        return passTexto;
    }

    public void setPassTexto(String passTexto) {
        this.passTexto = passTexto;
    }
    // endregion


    @Override
    public String toString() {
        return String.format("%s - %s %s", usuario, nombre != null ? nombre : "", apellido != null ? apellido : "");
    }
}
