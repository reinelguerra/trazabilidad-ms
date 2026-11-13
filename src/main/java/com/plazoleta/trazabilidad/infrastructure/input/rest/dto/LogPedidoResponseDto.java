package com.plazoleta.trazabilidad.infrastructure.input.rest.dto;

import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Registro guardado de un cambio de estado")
public record LogPedidoResponseDto(
        @Schema(example = "15") Long idPedido,
        @Schema(example = "4") Long idCliente,
        @Schema(example = "1") Long idRestaurante,
        @Schema(example = "7") Long idEmpleado,
        @Schema(example = "PENDIENTE") EstadoPedido estadoAnterior,
        @Schema(example = "EN_PREPARACION") EstadoPedido estadoNuevo,
        @Schema(description = "Fecha y hora en que el servicio registró el cambio (ISO-8601)",
                example = "2026-11-13T15:30:00") LocalDateTime fecha) {
}