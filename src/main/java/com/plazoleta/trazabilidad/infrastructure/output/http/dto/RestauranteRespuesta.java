package com.plazoleta.trazabilidad.infrastructure.output.http.dto;

/** Lo único que Trazabilidad lee del GET /restaurantes/{id} de plazoleta-ms. */
public record RestauranteRespuesta(Long id, Long idPropietario) {
}