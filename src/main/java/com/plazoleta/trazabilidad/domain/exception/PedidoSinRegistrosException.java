package com.plazoleta.trazabilidad.domain.exception;

public class PedidoSinRegistrosException extends DomainException {

    public PedidoSinRegistrosException(String mensaje) {
        super(mensaje);
    }
}