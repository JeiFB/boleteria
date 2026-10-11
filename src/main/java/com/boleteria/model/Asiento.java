package com.boleteria.model;

/** Asiento de un evento; vendido indica si ya fue adquirido. */
public class Asiento {

    private String idEvento;
    private String codigo;
    private String localidad;
    private String fila;
    private int numero;
    private double precio;
    private boolean vendido;

    public Asiento(String idEvento, String codigo, String localidad, String fila, int numero, double precio) {
        this.idEvento = idEvento;
        this.codigo = codigo;
        this.localidad = localidad;
        this.fila = fila;
        this.numero = numero;
        this.precio = precio;
        this.vendido = false;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isVendido() {
        return vendido;
    }

    public void setVendido(boolean vendido) {
        this.vendido = vendido;
    }
}
