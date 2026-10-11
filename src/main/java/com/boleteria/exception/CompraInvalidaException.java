package com.boleteria.exception;

/** La compra no cumple una regla de negocio (evento, asiento o datos invalidos). */
public class CompraInvalidaException extends Exception {

    public CompraInvalidaException(String mensaje) {
        super(mensaje);
    }
}
