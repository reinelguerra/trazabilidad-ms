package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.api.LogPedidoServicePort;
import com.plazoleta.trazabilidad.domain.model.LogPedido;

public class LogPedidoHandlerImpl implements LogPedidoHandler {

    private final LogPedidoServicePort logPedidoServicePort;

    public LogPedidoHandlerImpl(LogPedidoServicePort logPedidoServicePort) {
        this.logPedidoServicePort = logPedidoServicePort;
    }

    @Override
    public LogPedido registrarLog(LogPedido log) {
        return logPedidoServicePort.registrarLog(log);
    }
}