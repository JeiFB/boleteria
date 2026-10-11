package com.boleteria.model;

import java.util.ArrayList;
import java.util.List;

/** Compra realizada; se persiste en compras.json. */
public class Compra {

    private String id;
    private String idEvento;
    private Comprador comprador;
    private List<String> asientos = new ArrayList<>();
    private double subtotal;
    private double servicio;
    private double total;
    private String fecha;

    public Compra() {
    }

    public Compra(String id, String idEvento, Comprador comprador, List<String> asientos,
                  double subtotal, double servicio, double total, String fecha) {
        this.id = id;
        this.idEvento = idEvento;
        this.comprador = comprador;
        this.asientos = asientos;
        this.subtotal = subtotal;
        this.servicio = servicio;
        this.total = total;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public List<String> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<String> asientos) {
        this.asientos = asientos;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getServicio() {
        return servicio;
    }

    public void setServicio(double servicio) {
        this.servicio = servicio;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
