package com.plazoleta.trazabilidad.domain.model;

import java.time.LocalDateTime;

public record LogPedido(
        Long idPedido,
        Long idCliente,
        Long idRestaurante,
        Long idEmpleado,
        EstadoPedido estadoAnterior,
        EstadoPedido estadoNuevo,
        LocalDateTime fecha) {
}