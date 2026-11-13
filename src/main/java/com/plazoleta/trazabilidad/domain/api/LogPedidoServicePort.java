package com.plazoleta.trazabilidad.domain.api;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

public interface LogPedidoServicePort {

    LogPedido registrarLog(LogPedido log);
}