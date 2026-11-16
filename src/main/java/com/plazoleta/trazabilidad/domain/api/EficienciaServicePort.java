package com.plazoleta.trazabilidad.domain.api;

import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;

import java.util.List;

public interface EficienciaServicePort {

    /** Tiempo de cada pedido ENTREGADO del restaurante. Solo el propietario del restaurante. */
    List<EficienciaPedido> consultarTiempoPorPedido(Long idRestaurante, Long idPropietario);

    /** Tiempo medio por empleado, del más rápido al más lento. Solo el propietario del restaurante. */
    List<EficienciaEmpleado> consultarRankingEmpleados(Long idRestaurante, Long idPropietario);
}