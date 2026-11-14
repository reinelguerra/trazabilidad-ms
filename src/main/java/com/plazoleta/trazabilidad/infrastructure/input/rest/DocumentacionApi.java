package com.plazoleta.trazabilidad.infrastructure.input.rest;

public final class DocumentacionApi {

    public static final String NO_AUTENTICADO = "No autenticado: falta el token o no es válido";
    public static final String SIN_PERMISO = "Sin permiso: el rol del token no puede hacer esta acción";
    public static final String DATOS_INVALIDOS = "Datos inválidos o regla de negocio incumplida";

    private DocumentacionApi() {
    }
}