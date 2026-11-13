package com.plazoleta.trazabilidad.domain.util;

public final class MensajeError {

    public static final String TOKEN_REQUERIDO = "Debes iniciar sesión con un token válido";
    public static final String SIN_PERMISO = "Tu rol no tiene permiso para esta acción";
    public static final String PRIMER_LOG_DEBE_SER_PENDIENTE =
            "El primer registro de un pedido debe tener el estado PENDIENTE";
    public static final String ESTADO_REPETIDO = "El estado nuevo debe ser distinto al estado anterior";

    private MensajeError() {
    }
}