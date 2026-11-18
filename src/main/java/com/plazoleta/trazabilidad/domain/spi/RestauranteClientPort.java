package com.plazoleta.trazabilidad.domain.spi;

/** Puerto de salida hacia plazoleta-ms para saber de quién es un restaurante. */
public interface RestauranteClientPort {

    /**
     * @return el id del propietario del restaurante
     * @throws com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException si no existe
     */
    Long obtenerIdPropietario(Long idRestaurante);
}