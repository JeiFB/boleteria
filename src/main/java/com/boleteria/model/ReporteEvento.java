package com.boleteria.model;

/** Resultado del analisis de ventas de un evento. */
public class ReporteEvento {

    private final String idEvento;
    private final String nombre;
    private final int boletasVendidas;
    private final int totalAsientos;
    private final double ingresos;
    private final double ocupacion;

    public ReporteEvento(String idEvento, String nombre, int boletasVendidas,
                         int totalAsientos, double ingresos, double ocupacion) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.boletasVendidas = boletasVendidas;
        this.totalAsientos = totalAsientos;
        this.ingresos = ingresos;
        this.ocupacion = ocupacion;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getBoletasVendidas() {
        return boletasVendidas;
    }

    public int getTotalAsientos() {
        return totalAsientos;
    }

    public double getIngresos() {
        return ingresos;
    }

    public double getOcupacion() {
        return ocupacion;
    }
}
