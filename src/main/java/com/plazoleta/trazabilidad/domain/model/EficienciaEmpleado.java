package com.plazoleta.trazabilidad.domain.model;

/** Una fila del ranking: el tiempo medio de los pedidos que entregó un empleado. */
public record EficienciaEmpleado(
        int posicion,
        Long idEmpleado,
        long pedidosEntregados,
        long tiempoMedioSegundos) {
}