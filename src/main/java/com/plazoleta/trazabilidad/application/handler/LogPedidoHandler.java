package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

/** Puerto de entrada de la capa de aplicación para los logs: lo único que conoce el controller. */
public interface LogPedidoHandler {

    LogPedido registrarLog(LogPedido log);
}