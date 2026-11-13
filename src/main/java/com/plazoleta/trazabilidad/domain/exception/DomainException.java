package com.plazoleta.trazabilidad.domain.exception;

public class DomainException extends RuntimeException {

    public DomainException(String mensaje) {
        super(mensaje);
    }
}