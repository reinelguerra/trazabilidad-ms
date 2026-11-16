package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

import java.util.List;

/** Puerto de entrada de la capa de aplicación para los logs: lo único que conoce el controller. */
public interface LogPedidoHandler {

    LogPedido registrarLog(LogPedido log);

    List<LogPedido> consultarHistorial(Long idPedido, Long idCliente);
}