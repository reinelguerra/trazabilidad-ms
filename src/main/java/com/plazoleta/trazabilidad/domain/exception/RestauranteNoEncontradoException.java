package com.plazoleta.trazabilidad.domain.exception;

public class RestauranteNoEncontradoException extends DomainException {

    public RestauranteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}