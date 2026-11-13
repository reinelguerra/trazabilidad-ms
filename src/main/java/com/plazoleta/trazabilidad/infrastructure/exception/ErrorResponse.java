package com.plazoleta.trazabilidad.infrastructure.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Formato de error de la API")
public record ErrorResponse(
        @Schema(description = "Explicación del error", example = "El estadoNuevo es obligatorio") String mensaje,
        @Schema(description = "Código HTTP del error", example = "400") int status) {
}