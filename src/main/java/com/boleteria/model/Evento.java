package com.boleteria.model;

/**
 * Representa un evento cargado desde el archivo de eventos.
 */
public class Evento {

    private String idEvento;
    private String nombre;
    private String fecha;
    private String hora;
    private String lugar;
    private String tipoEvento;

    public Evento(String idEvento, String nombre, String fecha, String hora, String lugar, String tipoEvento) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
        this.tipoEvento = tipoEvento;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getLugar() {
        return lugar;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }
}
