package com.plazoleta.trazabilidad.infrastructure.exception;

public class ServicioPlazoletaNoDisponibleException extends RuntimeException {

    public ServicioPlazoletaNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}