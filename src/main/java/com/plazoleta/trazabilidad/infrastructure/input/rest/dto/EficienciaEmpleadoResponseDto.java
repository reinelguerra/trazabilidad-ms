package com.plazoleta.trazabilidad.infrastructure.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una fila del ranking de empleados: tiempo medio de los pedidos que entregó")
public record EficienciaEmpleadoResponseDto(
        @Schema(description = "Posición en el ranking (1 es el más rápido)", example = "1") int posicion,
        @Schema(example = "7") Long idEmpleado,
        @Schema(description = "Cantidad de pedidos entregados", example = "3") long pedidosEntregados,
        @Schema(description = "Promedio de duración de sus pedidos, en segundos", example = "1500")
        long tiempoMedioSegundos) {
}