package com.plazoleta.trazabilidad.domain.model;

import java.time.LocalDateTime;

/** Lo que tardó un pedido desde que se creó (PENDIENTE) hasta que se entregó (ENTREGADO). */
public record EficienciaPedido(
        Long idPedido,
        Long idEmpleado,
        LocalDateTime inicio,
        LocalDateTime fin,
        long duracionSegundos) {
}