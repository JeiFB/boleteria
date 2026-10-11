package com.boleteria.exception;

/** Error al leer o escribir un archivo (CSV, JSON o TXT). */
public class ArchivoException extends Exception {

    public ArchivoException(String mensaje) {
        super(mensaje);
    }

    public ArchivoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
