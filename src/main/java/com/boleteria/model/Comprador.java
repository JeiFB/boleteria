package com.boleteria.model;

/** Datos del titular de una compra (constructor vacio para el JSON). */
public class Comprador {

    private String nombre;
    private String documento;
    private String correo;

    public Comprador() {
    }

    public Comprador(String nombre, String documento, String correo) {
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
