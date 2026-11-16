package com.plazoleta.trazabilidad.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Lo que tardó un pedido entregado, desde que se creó hasta que se entregó")
public record EficienciaPedidoResponseDto(
        @Schema(example = "15") Long idPedido,
        @Schema(description = "Empleado que lo entregó", example = "7") Long idEmpleado,
        @Schema(description = "Cuando el pedido quedó PENDIENTE", example = "2026-11-16T15:00:00")
        LocalDateTime inicio,
        @Schema(description = "Cuando el pedido pasó a ENTREGADO", example = "2026-11-16T15:25:00")
        LocalDateTime fin,
        @Schema(description = "Diferencia entre inicio y fin, en segundos", example = "1500")
        long duracionSegundos) {
}