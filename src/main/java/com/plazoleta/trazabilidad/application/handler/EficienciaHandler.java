package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;

import java.util.List;

/** Puerto de entrada de la capa de aplicación para la eficiencia: lo único que conoce el controller. */
public interface EficienciaHandler {

    List<EficienciaPedido> consultarTiempoPorPedido(Long idRestaurante, Long idPropietario);

    List<EficienciaEmpleado> consultarRankingEmpleados(Long idRestaurante, Long idPropietario);
}