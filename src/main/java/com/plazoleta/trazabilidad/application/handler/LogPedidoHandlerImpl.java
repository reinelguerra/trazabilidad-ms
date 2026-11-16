package com.plazoleta.trazabilidad.application.handler;

import com.plazoleta.trazabilidad.domain.api.LogPedidoServicePort;
import com.plazoleta.trazabilidad.domain.model.LogPedido;

import java.util.List;

public class LogPedidoHandlerImpl implements LogPedidoHandler {

    private final LogPedidoServicePort logPedidoServicePort;

    public LogPedidoHandlerImpl(LogPedidoServicePort logPedidoServicePort) {
        this.logPedidoServicePort = logPedidoServicePort;
    }

    @Override
    public LogPedido registrarLog(LogPedido log) {
        return logPedidoServicePort.registrarLog(log);
    }

    @Override
    public List<LogPedido> consultarHistorial(Long idPedido, Long idCliente) {
        return logPedidoServicePort.consultarHistorial(idPedido, idCliente);
    }
}