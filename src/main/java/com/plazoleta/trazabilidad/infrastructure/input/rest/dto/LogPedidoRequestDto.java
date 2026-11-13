package com.plazoleta.trazabilidad.infrastructure.input.rest.dto;

import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Cambio de estado de un pedido que se va a registrar. La fecha la pone el servicio.")
public record LogPedidoRequestDto(
        @Schema(description = "Id del pedido", example = "15")
        @NotNull(message = MensajeError.ID_PEDIDO_INVALIDO)
        @Positive(message = MensajeError.ID_PEDIDO_INVALIDO)
        Long idPedido,

        @Schema(description = "Id del cliente dueño del pedido", example = "4")
        @NotNull(message = MensajeError.ID_CLIENTE_INVALIDO)
        @Positive(message = MensajeError.ID_CLIENTE_INVALIDO)
        Long idCliente,

        @Schema(description = "Id del restaurante del pedido", example = "1")
        @NotNull(message = MensajeError.ID_RESTAURANTE_INVALIDO)
        @Positive(message = MensajeError.ID_RESTAURANTE_INVALIDO)
        Long idRestaurante,

        @Schema(description = "Id del empleado asignado; null mientras ninguno se haya asignado", example = "7")
        @Positive(message = MensajeError.ID_EMPLEADO_INVALIDO)
        Long idEmpleado,

        @Schema(description = "Estado anterior; null en el primer registro del pedido", example = "PENDIENTE")
        EstadoPedido estadoAnterior,

        @Schema(description = "Estado al que pasó el pedido", example = "EN_PREPARACION")
        @NotNull(message = MensajeError.ESTADO_NUEVO_OBLIGATORIO)
        EstadoPedido estadoNuevo) {
}